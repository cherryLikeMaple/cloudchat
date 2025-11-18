package com.cherry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.dto.friendcCircleComment.CommentAddRequest;
import com.cherry.pojo.Comment;
import com.cherry.vo.CommentListResult;
import com.cherry.vo.CommentVO;

import java.util.List;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
public interface CommentService extends IService<Comment> {


    /**
     * 新增评论
     *
     * @param request       评论请求参数
     * @param currentUserId 当前登录用户 id（评论人）
     * @return 新增评论的 id
     */
    Long addComment(CommentAddRequest request, Long currentUserId);


    /**
     * 查询某条朋友圈下的评论数
     */
    CommentListResult listCommentsByCircleId(Long circleId);

    void deleteCommentById(Long commentId, Long loginUserId);
}
