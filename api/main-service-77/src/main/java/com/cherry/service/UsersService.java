package com.cherry.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.dto.user.UsersQueryRequest;
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
     * entity 转 vo
     * @param user
     * @return
     */
    UserVo getUserVo(Users user);

//    /**
//     * 获取当前登录的用户
//     * @param request
//     * @return
//     */
//    Users getLoginUser(HttpServletRequest request);
//    Users getLoginUser(String authorization);

    /**
     * 获取查询条件
     * @param usersQueryRequest
     * @return
     */
    QueryWrapper<Users> getQueryWrapper(UsersQueryRequest usersQueryRequest);

    /**
     * 
     */
}
