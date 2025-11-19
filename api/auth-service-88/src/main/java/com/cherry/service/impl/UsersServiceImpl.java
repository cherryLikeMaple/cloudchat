package com.cherry.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.constant.RedisKeys;
import com.cherry.enums.Sex;
import com.cherry.exceptions.GraceException;
import com.cherry.mapper.UsersMapper;
import com.cherry.pojo.Users;
import com.cherry.service.UsersService;
import com.cherry.utils.TokenUtils;
import com.cherry.vo.UserVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.cherry.grace.result.ResponseStatusEnum.*;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author cherry
 * @since 2025-11-09
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    private final static String SALT = "cherry";
    private final static String NICKNAME = "同学";
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public long userRegister(String account, String password, String checkPassword) {
        // 1.校验
        if (account.length() < 4) {
            GraceException.display(PARAMS_IS_TOO_SHORT);
        }
        if (password.length() < 6) {
            GraceException.display(PARAMS_IS_TOO_SHORT);
        }
        if (!password.equals(checkPassword)) {
            GraceException.display(ADMIN_PASSWORD_ERROR);
        }
        synchronized (account.intern()) {
            QueryWrapper<Users> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("account", account);
            Long count = this.baseMapper.selectCount(userQueryWrapper);
            if (count > 0) {
                GraceException.display(USER_ALREADY_EXIST_ERROR);
            }
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
            Users users = new Users();
            users.setAccount(account);
            users.setPassword(encryptPassword);
            users.setNickname(NICKNAME + RandomUtil.randomString(4));
            users.setSex(Sex.secret.type);
            boolean result = this.save(users);
            if (!result) {
                GraceException.display(SYSTEM_OPERATION_ERROR);
            }
            return users.getId();
        }
    }

    @Override
    public UserVo userLogin(String account, String password, String clientType, String deviceId) {
        // 1) 校验账号密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        Users user = this.lambdaQuery()
                .eq(Users::getAccount, account)
                .eq(Users::getPassword, encryptPassword)
                .one();

        if (user == null || Objects.equals(user.getIsDelete(), 1) ||
                "ban".equalsIgnoreCase(user.getUserRole())) {
            GraceException.display(USER_NOT_EXIST_ERROR);
        }

        String uid = String.valueOf(user.getId());

        /*
        
        单端登录
       
        // 2) 踢掉旧 token（如果有）
        String latestKey = RedisKeys.LOGIN_UID + uid;
        String oldToken = (String) redisTemplate.opsForValue().get(latestKey);
        if (StringUtils.isNotBlank(oldToken)) {
            redisTemplate.delete(RedisKeys.LOGIN_TOKEN + oldToken);
        }
        // 3) 生成并保存新 token（仅返回纯 token）
        String newToken = IdUtil.simpleUUID();
        Duration ttl = Duration.ofDays(7);
        redisTemplate.opsForValue().set(RedisKeys.LOGIN_TOKEN + newToken, uid, ttl);
        redisTemplate.opsForValue().set(latestKey, newToken, ttl);

        
        
        // 4) 组装返回
        UserVo vo = getUserVo(user);
        vo.setToken(newToken); // 只返回纯 token
        return vo;
        
         */

        /**
         * 实现多端会话策略: 同端互踢, 异端共存.
         */
        String userSessionsKey = RedisKeys.LOGIN_USER_SESSIONS + uid;
        Set<String> tokens = redisTemplate.opsForSet().members(userSessionsKey);

        if (tokens != null && !tokens.isEmpty()) {
            for (String token : tokens) {
                String oldSessionKey = RedisKeys.LOGIN_SESSION + token;
                Map<Object, Object> sessionMap = redisTemplate.opsForHash().entries(oldSessionKey);
                if (sessionMap == null || sessionMap.isEmpty()) {
                    redisTemplate.opsForSet().remove(userSessionsKey, token);
                    continue;
                }
                String oldClientType = (String) sessionMap.get("clientType");
                if (clientType != null && clientType.equalsIgnoreCase(oldClientType)) {
                    redisTemplate.delete(RedisKeys.LOGIN_TOKEN + token);
                    redisTemplate.delete(oldSessionKey);
                    redisTemplate.opsForSet().remove(userSessionsKey, token);
                }
            }
        }

        String newToken = IdUtil.simpleUUID();
        Duration ttl = Duration.ofDays(7);
        redisTemplate.opsForValue().set(RedisKeys.LOGIN_TOKEN + newToken, uid, ttl);

        String newSessionKey = RedisKeys.LOGIN_SESSION + newToken;
        Map<String, String> sessionInfo = new HashMap<>();
        sessionInfo.put("uid", uid);
        sessionInfo.put("clientType", clientType);
        sessionInfo.put("deviceId", deviceId);
        redisTemplate.opsForHash().putAll(newSessionKey, sessionInfo);
        redisTemplate.expire(newSessionKey, ttl);


        redisTemplate.opsForSet().add(userSessionsKey, newToken);

        redisTemplate.expire(userSessionsKey, ttl);


        UserVo vo = getUserVo(user);
        vo.setToken(newToken);
        return vo;
    }


    @Override
    public UserVo getUserVo(Users user) {
        if (user == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }

    @Override
    public Users getLoginUser(HttpServletRequest request) {
        // 1) 从请求解析 Bearer Token（Authorization: Bearer xxx）
        String token = TokenUtils.resolveToken(request);
        return getUsersBytoken(token);
    }

    @Override
    public Users getLoginUser(String auth) {
        if (auth == null) return null;
        if (auth.startsWith("Bearer ")) {
            auth = auth.substring(7);
        }
        return getUsersBytoken(auth.trim());
    }

    private Users getUsersBytoken(String token) {
        if (StringUtils.isBlank(token)) {
            GraceException.display(UN_LOGIN);
        }

        // 2) token -> uid（快速验证是否存在这个会话）
        String uid = (String) redisTemplate.opsForValue().get(RedisKeys.LOGIN_TOKEN + token);
        if (StringUtils.isBlank(uid)) {
            // 会话不存在 / token 过期 / 被踢
            GraceException.display(UN_LOGIN);
        }

        // 3) 进一步校验这个 token 的会话信息是否存在（防止只删了 session hash 的情况）
        String sessionKey = RedisKeys.LOGIN_SESSION + token;
        Boolean hasSession = redisTemplate.hasKey(sessionKey);
        if (Boolean.FALSE.equals(hasSession)) {
            GraceException.display(UN_LOGIN);
        }

        // 【注意】这里不再做 “最新 token 校验”（LOGIN_UID + uid），
        // 因为我们现在是多端多会话模型，不存在“单一最新 token”的概念了。

        // 4) 查用户（可加本地/Redis 缓存）
        Users user = this.getById(uid);
        if (user == null || Objects.equals(user.getIsDelete(), 1) ||
                "ban".equalsIgnoreCase(user.getUserRole())) {
            GraceException.display(UN_LOGIN);
        }

        // 5) （可选）滑动续期 -> 刷新当前会话的过期时间
        Duration ttl = Duration.ofDays(7);
        redisTemplate.expire(RedisKeys.LOGIN_TOKEN + token, ttl);
        redisTemplate.expire(sessionKey, ttl);
        // 可选：顺便刷新 userSessions 的 TTL
        redisTemplate.expire(RedisKeys.LOGIN_USER_SESSIONS + uid, ttl);

        return user;
    }


    @Override
    public boolean logout(HttpServletRequest request) {
        // 1) 获取 token
        String token = TokenUtils.resolveToken(request);
        if (StringUtils.isBlank(token)) {
            GraceException.display(PARAMS_NULL);
        }

        // 2) token -> uid
        String tokenKey = RedisKeys.LOGIN_TOKEN + token;
        String uid = (String) redisTemplate.opsForValue().get(tokenKey);
        if (StringUtils.isBlank(uid)) {
            // token 无效 / 已注销 / 被踢 / 过期
            return true; // 幂等：当作成功
        }

        // 3) 删除 token -> uid 映射
        redisTemplate.delete(tokenKey);

        // 4) 删除 token 对应的会话详情
        String sessionKey = RedisKeys.LOGIN_SESSION + token;
        redisTemplate.delete(sessionKey);

        // 5) 从该用户的会话集合中移除该 token
        String userSessionsKey = RedisKeys.LOGIN_USER_SESSIONS + uid;
        redisTemplate.opsForSet().remove(userSessionsKey, token);

        return true;
    }

    @Override
    public boolean logoutAll(HttpServletRequest request) {
        String token = TokenUtils.resolveToken(request);
        if (StringUtils.isBlank(token)) {
            GraceException.display(PARAMS_NULL);
        }

        // 根据 token 查 uid
        String uid = (String) redisTemplate.opsForValue().get(RedisKeys.LOGIN_TOKEN + token);
        if (StringUtils.isBlank(uid)) return true;

        // 1) 拿到该用户全部 token
        String sessionsKey = RedisKeys.LOGIN_USER_SESSIONS + uid;
        Set<Object> tokens = redisTemplate.opsForSet().members(sessionsKey);

        if (tokens != null) {
            for (Object tk : tokens) {
                String t = (String) tk;
                redisTemplate.delete(RedisKeys.LOGIN_TOKEN + t);
                redisTemplate.delete(RedisKeys.LOGIN_SESSION + t);
            }
        }

        // 2) 删除整个 token 集合
        redisTemplate.delete(sessionsKey);

        return true;
    }


}
