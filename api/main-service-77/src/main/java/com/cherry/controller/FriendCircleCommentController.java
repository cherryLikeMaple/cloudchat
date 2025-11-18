package com.cherry.controller;

import com.cherry.dto.friendcCircleComment.CommentAddRequest;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.pojo.Users;
import com.cherry.service.CommentService;
import com.cherry.service.UsersService;
import com.cherry.vo.CommentVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 朋友圈表 前端控制器
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/friendCircleComment")
public class FriendCircleCommentController {

    @Resource
    private UsersService usersService;
    @Resource
    private CommentService commentService;

    /**
     * 返回朋友圈下的所有id.
     *
     * @param circleId
     * @return
     */
    @GetMapping("/comments/get")
    public GraceJSONResult listComments(Long circleId) {
        List<CommentVO> list = commentService.listCommentsByCircleId(circleId).getList();
        return GraceJSONResult.ok(list);
    }

    @PostMapping("/comment/add")
    public GraceJSONResult addComment(@RequestBody CommentAddRequest commentAddRequest,
                                      HttpServletRequest request) {
        Users loginUser = usersService.getLoginUser(request);
        if (loginUser == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        Long commentId = commentService.addComment(commentAddRequest, loginUser.getId());
        return GraceJSONResult.ok(commentId);
    }

    @PostMapping("/comment/delete")
    public GraceJSONResult deleteComment( Long commentId,
                                         HttpServletRequest request) {

        Users loginUser = usersService.getLoginUser(request);
        if (loginUser == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        commentService.deleteCommentById(commentId, loginUser.getId());

        return GraceJSONResult.ok();
    }
}
