package com.cherry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.pojo.Users;
import com.cherry.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author cherry
 * @since 2025-11-09
 */
public interface UsersService extends IService<Users> {


    /**
     * 用户注册
     *
     * @param account
     * @param password
     * @param checkPassword
     * @return
     */
    long userRegister(String account, String password, String checkPassword);

    /**
     * 用户登录
     *
     * @param account
     * @param password
     * @return
     */
    UserVo userLogin(String account, String password, String clientType, String deviceId);

    /**
     * entity 转 vo
     *
     * @param user
     * @return
     */
    UserVo getUserVo(Users user);

    /**
     * 获取当前登录的用户
     *
     * @param request
     * @return
     */
    Users getLoginUser(HttpServletRequest request);

    /**
     * 内部微服务使用
     * @param authorization
     * @return
     */
    Users getLoginUser(String authorization);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean logout(HttpServletRequest request);

    /**
     * 退出所有终端的接口
     * @param request
     * @return
     */
    boolean logoutAll(HttpServletRequest request);
}
