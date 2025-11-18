package com.cherry.dto.friendcCircleComment;

import lombok.Data;

/**
 * @author cherry
 */
@Data
public class CommentAddRequest {

    /**
     * 朋友圈 id
     */
    private Long friendCircleId;

    /**
     * 父评论 id（如果是回复评论，则必传；一级评论可为 null）
     */
    private Long parentCommentId;

    /**
     * 被回复的用户 id（可选：
     *  - 一级评论可以不传，后端用朋友圈主人
     *  - 回复评论不传时，后端用父评论的 commentUserId
     */
    private Long replyToUserId;

    /**
     * 评论内容
     */
    private String commentContent;
}
