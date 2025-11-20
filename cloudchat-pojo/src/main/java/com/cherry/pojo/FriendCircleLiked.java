package com.cherry.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 点赞朋友圈的朋友
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
@TableName("friend_circle_liked")
public class FriendCircleLiked implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 朋友圈归属用户的id
     */
    private Long belongUserId;

    /**
     * 点赞的那个朋友圈id
     */
    private Long friendCircleId;

    /**
     * 点赞的那个用户id
     */
    private Long likedUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

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

    public Long getFriendCircleId() {
        return friendCircleId;
    }

    public void setFriendCircleId(Long friendCircleId) {
        this.friendCircleId = friendCircleId;
    }

    public Long getLikedUserId() {
        return likedUserId;
    }

    public void setLikedUserId(Long likedUserId) {
        this.likedUserId = likedUserId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "FriendCircleLiked{" +
        "id = " + id +
        ", belongUserId = " + belongUserId +
        ", friendCircleId = " + friendCircleId +
        ", likedUserId = " + likedUserId +
        ", createTime = " + createTime +
        "}";
    }
}
