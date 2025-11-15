package com.cherry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.constant.RedisKeys;
import com.cherry.dto.user.UsersQueryRequest;
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

import java.time.Duration;
import java.time.LocalDate;
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
    public Users getLoginUser(String authorization) {
        // 1) 从请求解析 Bearer Token（Authorization: Bearer xxx）
        String auth = authorization;
        if (auth == null) return null;
        if (auth.startsWith("Bearer ")) {
            auth = auth.substring(7);
        }
        String token = auth.trim();
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

        Duration ttl = Duration.ofDays(7);
        redisTemplate.expire(RedisKeys.LOGIN_TOKEN + token, ttl);
        redisTemplate.expire(RedisKeys.LOGIN_UID + uid, ttl);

        return user;
    }

        @Override
        public QueryWrapper<Users> getQueryWrapper(UsersQueryRequest usersQueryRequest) {
            if (usersQueryRequest == null) {
                GraceException.display(PARAMS_NULL);
            }
    
            Long id = usersQueryRequest.getId();
            String account = usersQueryRequest.getAccount();
            String mobile = usersQueryRequest.getMobile();
            String nickname = usersQueryRequest.getNickname();
            String realName = usersQueryRequest.getRealName();
            Integer sex = usersQueryRequest.getSex();
            String email = usersQueryRequest.getEmail();
            LocalDate birthday = usersQueryRequest.getBirthday();
            String country = usersQueryRequest.getCountry();
            String province = usersQueryRequest.getProvince();
            String city = usersQueryRequest.getCity();
            String userRole = usersQueryRequest.getUserRole();
            String sortField = usersQueryRequest.getSortField();
            String sortOrder = usersQueryRequest.getSortOrder();
    
            QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
            // 构造查询条件
            usersQueryWrapper.eq(id != null, "id", id);
            usersQueryWrapper.eq(StringUtils.isNotBlank(account), "account", account);
            usersQueryWrapper.eq(StringUtils.isNotBlank(mobile), "mobile", mobile);
            usersQueryWrapper.eq(sex != null, "sex", sex);
            usersQueryWrapper.eq(StringUtils.isNotBlank(country), "country", country);
            usersQueryWrapper.eq(StringUtils.isNotBlank(province), "province", province);
            usersQueryWrapper.eq(StringUtils.isNotBlank(city), "city", city);
            usersQueryWrapper.eq(birthday != null, "birthday", birthday);
            usersQueryWrapper.eq(StringUtils.isNotBlank(userRole), "user_role", userRole);
            usersQueryWrapper.like(StringUtils.isNotBlank(nickname), "nickname", nickname);
            usersQueryWrapper.like(StringUtils.isNotBlank(realName), "real_name", realName);
            usersQueryWrapper.like(StringUtils.isNotBlank(email), "email", email);
            
            return usersQueryWrapper;
        }

}
