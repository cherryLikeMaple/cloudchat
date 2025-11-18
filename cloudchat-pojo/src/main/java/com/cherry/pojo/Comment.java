package com.cherry.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 朋友圈评论表
 * </p>
 *
 * @author cherry
 * @since 2025-11-18
 */
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 朋友圈所属用户id（这条朋友圈的主人）
     */
    private Long belongUserId;

    /**
     * 父评论id（如果是回复，则此字段为父评论）
     */
    private Long fatherId;

    /**
     * 关联的朋友圈id
     */
    private Long friendCircleId;

    /**
     * 评论人用户id
     */
    private Long commentUserId;

    /**
     * 被回复的用户id（一级评论可为朋友圈主人或空）
     */
    private Long replyToUserId;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 评论时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 是否删除：0-正常，1-已删除
     */
    private Boolean isDelete;

    /**
     * 状态：0-正常，1-屏蔽/违规
     */
    private Boolean status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBelongUserId() {
        return belongUserId;
    }

    public void setBelongUserId(Long belongUserId) {
        this.belongUserId = belongUserId;
    }

    public Long getFatherId() {
        return fatherId;
    }

    public void setFatherId(Long fatherId) {
        this.fatherId = fatherId;
    }

    public Long getFriendCircleId() {
        return friendCircleId;
    }

    public void setFriendCircleId(Long friendCircleId) {
        this.friendCircleId = friendCircleId;
    }

    public Long getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(Long commentUserId) {
        this.commentUserId = commentUserId;
    }

    public Long getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(Long replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Comment{" +
        "id = " + id +
        ", belongUserId = " + belongUserId +
        ", fatherId = " + fatherId +
        ", friendCircleId = " + friendCircleId +
        ", commentUserId = " + commentUserId +
        ", replyToUserId = " + replyToUserId +
        ", commentContent = " + commentContent +
        ", createdTime = " + createdTime +
        ", updatedTime = " + updatedTime +
        ", isDelete = " + isDelete +
        ", status = " + status +
        "}";
    }
}
