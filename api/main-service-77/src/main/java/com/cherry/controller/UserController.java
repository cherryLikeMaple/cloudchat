package com.cherry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cherry.api.feign.UserInfoMicroServiceFeign;
import com.cherry.dto.user.UserUpdateRequest;
import com.cherry.dto.user.UsersQueryRequest;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.pojo.Users;
import com.cherry.service.UsersService;
import com.cherry.vo.TinyUserVO;
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
    @Resource
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;

    @PostMapping("/update/my")
    public GraceJSONResult modify(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Users users = new Users();
        BeanUtils.copyProperties(userUpdateRequest, users);
        users.setId(loginUser.getId());
        boolean result = usersService.updateById(users);
        return GraceJSONResult.ok(result);
    }

    /**
     * 返回朋友圈的用户信息.
     *
     * @param userId
     * @return
     */
    @PostMapping("/get/user/circle/evo")
    public GraceJSONResult getCircleUserVo(Long userId) {

        return GraceJSONResult.ok(TinyUserVO.fromEntity(usersService.getById(userId)));
    }

    /**
     * 返回用户的vo类型.
     *
     * @param userId
     * @return
     */
    @PostMapping("/get/user/vo")
    public GraceJSONResult getUserVo(Long userId) {
        return GraceJSONResult.ok(usersService.getUserVo(usersService.getById(userId)));
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public GraceJSONResult getLoginUser(HttpServletRequest request) {
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        return GraceJSONResult.ok(usersService.getUserVo(loginUser));
    }


    /**
     * 账号或者昵称查询好友
     *
     * @param request
     * @param usersQueryRequest
     * @return
     */
    @PostMapping("/queryFriend")
    public GraceJSONResult queryFriend(HttpServletRequest request, @RequestBody UsersQueryRequest usersQueryRequest) {
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        String account = usersQueryRequest.getAccount();

        if (StringUtils.isAllBlank(account)) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        LambdaQueryWrapper<Users> usersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        usersLambdaQueryWrapper.eq(Users::getAccount, account).or().eq(Users::getRealName, usersQueryRequest.getRealName())
                .or().eq(Users::getNickname, usersQueryRequest.getNickname());
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
