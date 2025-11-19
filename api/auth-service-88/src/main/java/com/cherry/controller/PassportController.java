package com.cherry.controller;

import com.cherry.dto.user.UserLoginRequest;
import com.cherry.dto.user.UserRegisterRequest;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.pojo.Users;
import com.cherry.service.UsersService;
import com.cherry.vo.UserVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import static com.cherry.grace.result.ResponseStatusEnum.PARAMS_NULL;

/**
 * @author cherry
 */
@RestController
@RequestMapping("/passport")
public class PassportController {


    @Resource
    private UsersService usersService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public GraceJSONResult register(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 1.判断
        if (userRegisterRequest == null) {
            GraceException.display(PARAMS_NULL);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword)) {
            GraceException.display(PARAMS_NULL);
        }
        long result = usersService.userRegister(userAccount, userPassword, checkPassword);
        return GraceJSONResult.ok(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public GraceJSONResult login(@RequestBody UserLoginRequest userLoginRequest) {
        // 1.判断
        if (userLoginRequest == null) {
            GraceException.display(PARAMS_NULL);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            GraceException.display(PARAMS_NULL);
        }
        UserVo userVo = usersService.userLogin(userAccount, userPassword
                , userLoginRequest.getClientType(), userLoginRequest.getDeviceId());
        return GraceJSONResult.ok(userVo);
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
        return GraceJSONResult.ok(usersService.getUserVo(loginUser));
    }

    /**
     * 用户注销
     *
     * @return
     */
    @PostMapping("/logout")
    public GraceJSONResult logout(@RequestBody HttpServletRequest request) {
        boolean result = usersService.logout(request);
        return GraceJSONResult.ok(result);
    }

    /**
     * 用户注销(退出所有终端)
     *
     * @return
     */
    @PostMapping("/logoutAll")
    public GraceJSONResult logoutAll(@RequestBody HttpServletRequest request) {
        boolean result = usersService.logoutAll(request);
        return GraceJSONResult.ok(result);
    }

    /**
     * 内部调用服务接口
     *
     * @param authorization
     * @return
     */
    @GetMapping("/internal/user/me")
    public Users getLoginUser(@RequestHeader("Authorization") String authorization) {
        return usersService.getLoginUser(authorization);
    }

}
