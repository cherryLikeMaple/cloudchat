package com.cherry.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
@Data
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

    private int visibleScope;
}
