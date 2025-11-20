package com.cherry.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author cherry
 */
@Data
public class CommentVO implements Serializable {

    /**
     * 评论 id
     */
    private Long commentId;

    /**
     * 朋友圈 id（有时前端会用，保留也行）
     */
    private Long friendCircleId;

    /**
     * 父评论 id（null 或 0 表示一级评论）
     */
    private Long parentCommentId;

    /**
     * 评论者信息
     */
    private TinyUserVO commentUser;

    /**
     * 被回复的用户（楼中楼时用，没有就为 null）
     */
    private TinyUserVO replyToUser;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子评论
     */
    private List<CommentVO> children;
}

