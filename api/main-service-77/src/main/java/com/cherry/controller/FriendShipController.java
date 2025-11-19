package com.cherry.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cherry.api.feign.UserInfoMicroServiceFeign;
import com.cherry.enums.YesOrNo;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.pojo.Users;
import com.cherry.service.FriendshipService;
import com.cherry.vo.FriendshipVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author cherry
 */
@RestController
@RequestMapping("/friendShip")
@Slf4j
public class FriendShipController {

    @Resource
    private FriendshipService friendshipService;
    @Resource
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;

    /**
     * 根据id得到单个好友关系
     *
     * @param friendId
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public GraceJSONResult getFriendShipVo(Long friendId, HttpServletRequest request) {
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        if (friendId == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        return GraceJSONResult.ok(friendshipService.getFriendShipVo(friendId, loginUser.getId()));
    }

    /**
     * 得到我所有的好友(包括正常好友和黑名单)
     *
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     */
    @GetMapping("/friends/all/vo")
    public GraceJSONResult listAll(@RequestParam(defaultValue = "1", name = "currentPage") Long currentPage,
                                   @RequestParam(defaultValue = "5", name = "pageSize") Long pageSize,
                                   HttpServletRequest request) {
        return GraceJSONResult.ok(friendshipService.getFriendShipVoPage(currentPage, pageSize, request, null));
    }

    /**
     * 返回正常好友
     *
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     */
    @GetMapping("/friends/vo")
    public GraceJSONResult listFriends(@RequestParam Long currentPage,
                                       @RequestParam Long pageSize,
                                       HttpServletRequest request) {
        Page<FriendshipVo> page = friendshipService.getFriendShipVoPage(currentPage, pageSize, request, 0);
        return GraceJSONResult.ok(page);
    }

    /**
     * 返回我的黑名单朋友
     *
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     */
    @GetMapping("/blacklist/vo")
    public GraceJSONResult listBlack(@RequestParam Long currentPage,
                                     @RequestParam Long pageSize,
                                     HttpServletRequest request) {
        Page<FriendshipVo> page = friendshipService.getFriendShipVoPage(currentPage, pageSize, request, 1);
        return GraceJSONResult.ok(page);
    }

    /**
     * 更新备注
     *
     * @param request
     * @param friendId
     * @param remark
     * @return
     */
    // note 下面这俩个基本是差不多的功能, 但是拆分为俩个接口, 可以实现业务解耦, 防止并发高同时打在一个接口上
    // note service 可以不用进行拆分. 
    @PostMapping("/updateFriendRemark")
    public GraceJSONResult updateFriendRemark(HttpServletRequest request, Long friendId, String remark) {
        if (friendId == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        if (StringUtils.isBlank(remark)) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        friendshipService.updateFriendRemark(loginUser.getId(), friendId, remark);
        return GraceJSONResult.ok();
    }

    /**
     * 加入黑名单
     *
     * @param request
     * @param friendId
     * @return
     */
    @PostMapping("/toBlack")
    public GraceJSONResult toBlack(HttpServletRequest request, Long friendId) {
        if (friendId == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        friendshipService.updateBlackList(loginUser.getId(), friendId, YesOrNo.YES.type);
        return GraceJSONResult.ok();
    }

    /**
     * 移除黑名单
     *
     * @param request
     * @param friendId
     * @return
     */
    @PostMapping("/outBlack")
    public GraceJSONResult outBlack(HttpServletRequest request, Long friendId) {
        if (friendId == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        friendshipService.updateBlackList(loginUser.getId(), friendId, YesOrNo.NO.type);
        return GraceJSONResult.ok();
    }

    /**
     * 删除好友
     *
     * @param request
     * @param friendId
     * @return
     */
    @PostMapping("/delete")
    public GraceJSONResult delete(HttpServletRequest request, Long friendId) {
        if (friendId == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        friendshipService.deleteFriend(loginUser.getId(), friendId);
        return GraceJSONResult.ok();
    }
}
