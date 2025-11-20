package com.cherry.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 朋友圈表
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
@TableName("friend_circle")
public class FriendCircle implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 发朋友圈的用户id
     */
    private Long userId;

    /**
     * 文字内容
     */
    private String words;

    /**
     * 图片内容，url用逗号分割
     */
    private String images;

    /**
     * 视频url
     */
    private String video;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "FriendCircle{" +
        "id = " + id +
        ", userId = " + userId +
        ", words = " + words +
        ", images = " + images +
        ", video = " + video +
        ", createTime = " + createTime +
        "}";
    }
}
