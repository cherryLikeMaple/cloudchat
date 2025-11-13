package com.cherry.controller;

import com.cherry.dto.user.UserUpdateRequest;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.pojo.Users;
import com.cherry.service.UsersService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userinfo")
public class UserController {

    @Resource
    private UsersService usersService;

    @PostMapping("/update/my")
    public GraceJSONResult modify(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        Users loginUser = usersService.getLoginUser(request);
        Users users = new Users();
        BeanUtils.copyProperties(userUpdateRequest, users);
        users.setId(loginUser.getId());
        boolean result = usersService.updateById(users);
        return GraceJSONResult.ok(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public GraceJSONResult getLoginUser(HttpServletRequest request) {
        Users loginUser = usersService.getLoginUser(request);
        return GraceJSONResult.ok(loginUser);
    }


    /**
     * 获取当前登录用户
     *
     * @return
     */
    @GetMapping("/internal/user/me")
    public Users getLoginUser(@RequestHeader("Authorization") String authorization) {
        return usersService.getLoginUser(authorization);
    }
}
