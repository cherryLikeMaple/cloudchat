package com.cherry.controller;

import com.cherry.dto.friendCycle.CreateFriendCircleDTO;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.pojo.Users;
import com.cherry.service.FriendCircleLikedService;
import com.cherry.service.FriendCircleService;
import com.cherry.service.UsersService;
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
@RequestMapping("/friendCircleLiked")
public class FriendCircleLikedController {

    @Resource
    private UsersService usersService;
    @Resource
    private FriendCircleLikedService friendCircleLikedService;

    @PostMapping("/like")
    public GraceJSONResult toggleLike(Long circleId, HttpServletRequest request) {

        Users loginUser = usersService.getLoginUser(request);
        Long userId = loginUser.getId();

        boolean liked = friendCircleLikedService.likeOrUnlike(userId, circleId);

        return GraceJSONResult.ok(liked ? "点赞成功" : "取消点赞");
    }
}
