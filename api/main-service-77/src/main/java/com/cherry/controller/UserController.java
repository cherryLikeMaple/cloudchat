package com.cherry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cherry.dto.user.UserUpdateRequest;
import com.cherry.dto.user.UsersQueryRequest;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.pojo.Users;
import com.cherry.service.UsersService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
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


    /**
     * 通过手机号或者账号查询好友
     * @param request
     * @param usersQueryRequest
     * @return
     */
    @PostMapping("/queryFriend")
    public GraceJSONResult queryFriend(HttpServletRequest request, @RequestBody UsersQueryRequest usersQueryRequest) {
        Users loginUser = usersService.getLoginUser(request);
        String account = usersQueryRequest.getAccount();
        String mobile = usersQueryRequest.getMobile();
        
        if (StringUtils.isAllBlank(account, mobile)) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        LambdaQueryWrapper<Users> usersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        usersLambdaQueryWrapper.eq(Users::getAccount, account).or().eq(Users::getMobile, mobile);
        Users friend = usersService.getOne(usersLambdaQueryWrapper, false);
        if (friend == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FRIEND_NOT_EXIST_ERROR);
        }
        if (loginUser.getId().equals(friend.getId())) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.CAN_NOT_ADD_SELF_FRIEND_ERROR);
        }
        
        return GraceJSONResult.ok(usersService.getUserVo(friend));
    }
}
