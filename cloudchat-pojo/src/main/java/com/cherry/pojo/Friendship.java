package com.cherry.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 朋友关系表
 * </p>
 *
 * @author cherry
 * @since 2025-11-15
 */
public class Friendship implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 自己的用户id
     */
    private String myId;

    /**
     * 我朋友的id
     */
    private String friendId;

    /**
     * 好友的备注名
     */
    private String friendRemark;

    /**
     * 是否消息免打扰，0-打扰，不忽略消息(默认)；1-免打扰，忽略消息
     */
    private Integer isMsgIgnore;

    /**
     * 是否拉黑，0-好友(默认)；1-已拉黑
     */
    private Integer isBlack;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendRemark() {
        return friendRemark;
    }

    public void setFriendRemark(String friendRemark) {
        this.friendRemark = friendRemark;
    }

    public Integer getIsMsgIgnore() {
        return isMsgIgnore;
    }

    public void setIsMsgIgnore(Integer isMsgIgnore) {
        this.isMsgIgnore = isMsgIgnore;
    }

    public Integer getIsBlack() {
        return isBlack;
    }

    public void setIsBlack(Integer isBlack) {
        this.isBlack = isBlack;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Friendship{" +
        "id = " + id +
        ", myId = " + myId +
        ", friendId = " + friendId +
        ", friendRemark = " + friendRemark +
        ", isMsgIgnore = " + isMsgIgnore +
        ", isBlack = " + isBlack +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        "}";
    }
}
