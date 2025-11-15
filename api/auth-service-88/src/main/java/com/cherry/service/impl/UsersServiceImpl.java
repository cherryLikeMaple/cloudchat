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
import java.util.Objects;

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
    public UserVo userLogin(String account, String password) {
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
        if (StringUtils.isBlank(token)) {
            GraceException.display(UN_LOGIN); // 或 PARAMS_NULL
        }

        // 2) token -> uid
        String uid = (String) redisTemplate.opsForValue().get(RedisKeys.LOGIN_TOKEN + token);
        if (StringUtils.isBlank(uid)) {
            GraceException.display(UN_LOGIN); // 会话不存在或已过期/被踢
        }

        // 3) 校验“是否最新 token”
        String latest = (String) redisTemplate.opsForValue().get(RedisKeys.LOGIN_UID + uid);
        if (!token.equals(latest)) {
            GraceException.display(TICKET_INVALID); // 自定义业务码：你的账号在别处登录
        }

        // 4) 查用户（可加本地/Redis 缓存）
        Users user = this.getById(uid);
        if (user == null || Objects.equals(user.getIsDelete(), 1) ||
                "ban".equalsIgnoreCase(user.getUserRole())) {
            GraceException.display(UN_LOGIN);
        }

        // 5) （可选）滑动续期
        Duration ttl = Duration.ofDays(7);
        redisTemplate.expire(RedisKeys.LOGIN_TOKEN + token, ttl);
        redisTemplate.expire(RedisKeys.LOGIN_UID + uid, ttl);

        return user;
    }

    @Override
    public boolean logout(HttpServletRequest request) {
        String token = TokenUtils.resolveToken(request);
        if (token == null) {
            GraceException.display(PARAMS_NULL);
        }
        String tokenKey = RedisKeys.LOGIN_TOKEN + token;
        redisTemplate.delete(tokenKey);
        return true;
    }
}
