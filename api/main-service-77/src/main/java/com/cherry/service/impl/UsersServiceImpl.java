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
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;

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
        // 1.查询数据库
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
        usersQueryWrapper.eq("account", account);
        usersQueryWrapper.eq("password", encryptPassword);
        // 2.判断用户是否存在
        Users users = this.baseMapper.selectOne(usersQueryWrapper);
        if (users == null) {
            GraceException.display(USER_NOT_EXIST_ERROR);
        }
        // 3.使用分布式redis存储用户态.
        String tokenKey = RedisKeys.LOGIN_TOKEN + IdUtil.simpleUUID();
        redisTemplate.opsForValue().set(tokenKey, users.getId(), Duration.ofDays(7));
        UserVo userVo = this.getUserVo(users);
        userVo.setToken(tokenKey);
        return userVo;
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
        String token = TokenUtils.resolveToken(request);
        if (token == null) {
            GraceException.display(PARAMS_NULL);
        }
        String tokenKey = RedisKeys.LOGIN_TOKEN + token;
        String userId = (String) redisTemplate.opsForValue().get(tokenKey);
        if (userId == null) {
            GraceException.display(USER_NOT_EXIST_ERROR);
        }
        Users users = this.baseMapper.selectById(userId);
        if (users == null) {
            GraceException.display(USER_NOT_EXIST_ERROR);
        }
        return users;
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
