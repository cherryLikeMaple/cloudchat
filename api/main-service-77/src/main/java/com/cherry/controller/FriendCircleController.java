package com.cherry.controller;

import com.cherry.api.feign.UserInfoMicroServiceFeign;
import com.cherry.dto.friendCycle.CreateFriendCircleDTO;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.pojo.Users;
import com.cherry.service.FriendCircleService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 朋友圈表 前端控制器
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/friendCircle")
public class FriendCircleController {


    @Resource
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;
    @Resource
    private FriendCircleService friendCircleService;

    /**
     * 发布朋友圈.
     *
     * @param createFriendCircleDTO
     * @param request
     * @return
     */
    @PostMapping("/publish")
    public GraceJSONResult publish(@RequestBody CreateFriendCircleDTO createFriendCircleDTO, HttpServletRequest request) {
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long userId = loginUser.getId();
        friendCircleService.publish(createFriendCircleDTO, userId);
        return GraceJSONResult.ok();
    }

    /**
     * 查看：我 + 我所有好友 的朋友圈（分页）
     */
    @GetMapping("/listAll")
    public GraceJSONResult listAll(@RequestParam Integer page,
                                   @RequestParam Integer pageSize,
                                   HttpServletRequest request) {

        // 当前登录用户
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long loginUserId = loginUser.getId();

        return GraceJSONResult.ok(
                friendCircleService.listAllFriendCircle(loginUserId, page, pageSize)
        );
    }
    

    /**
     * 查看：某一个好友的朋友圈（分页）
     */
    @GetMapping("/listByUser")
    public GraceJSONResult listByUser(@RequestParam Long friendId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize,
                                      HttpServletRequest request) {
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long loginUserId = loginUser.getId();
        return GraceJSONResult.ok(
                friendCircleService.listFriendCircleByUser(loginUserId, friendId, page, pageSize)
        );
    }

    /**
     * 根据id返回朋友圈.
     *
     * @param circleId
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public GraceJSONResult listById(Long circleId,
                                    HttpServletRequest request) {
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long loginUserId = loginUser.getId();
        return GraceJSONResult.ok(friendCircleService.getFriendCircle(loginUserId, circleId));
    }
}
