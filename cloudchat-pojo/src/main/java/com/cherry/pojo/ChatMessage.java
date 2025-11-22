package com.cherry.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 聊天信息存储表
 * </p>
 *
 * @author cherry
 * @since 2025-11-22
 */
@TableName("chat_message")
@Data
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 前端生成的消息ID（用于去重和状态同步）
     */
    private String msgId;

    /**
     * 发送者用户id
     */
    private Long senderId;

    /**
     * 接收者id（用户或群）
     */
    private Long receiverId;

    /**
     * 接收者类型：1=用户，2=群组
     */
    private int chatType;

    /**
     * 文本消息内容
     */
    private String content;

    /**
     * 消息类型：1=文本，2=图片，3=视频，4=语音等
     */
    private int msgType;

    /**
     * 消息时间（发送/接收时间）
     */
    private LocalDateTime chatTime;

    /**
     * 视频封面地址
     */
    private String videoCoverUrl;

    /**
     * 视频地址
     */
    private String mediaUrl;

    /**
     * 视频宽度
     */
    private Integer mediaWidth;

    /**
     * 视频高度
     */
    private Integer mediaHeight;

    /**
     * 视频时长（秒）
     */
    private Integer videoTimes;

    /**
     * 语音地址
     */
    private String voiceUrl;

    /**
     * 语音时长（秒）
     */
    private Integer voiceDuration;

    /**
     * 是否已读：0=未读，1=已读
     */
    private int isRead;

    /**
     * 是否删除：0=否，1=是
     */
    private int isDelete;
    
}
