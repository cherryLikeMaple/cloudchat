package com.cherry.controller;

import com.cherry.api.feign.UserInfoMicroServiceFeign;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.pojo.Users;
import com.cherry.service.FriendCircleLikedService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 朋友圈表 前端控制器
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/friendCircleLiked")
public class FriendCircleLikedController {

    @Resource
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;
    @Resource
    private FriendCircleLikedService friendCircleLikedService;

    @PostMapping("/like")
    public GraceJSONResult toggleLike(Long circleId, HttpServletRequest request) {

        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long userId = loginUser.getId();

        boolean liked = friendCircleLikedService.likeOrUnlike(userId, circleId);

        return GraceJSONResult.ok(liked ? "点赞成功" : "取消点赞");
    }
}
