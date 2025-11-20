package com.cherry.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天消息实体，对应 chat_message 表
 */
@Data
@TableName("chat_message")
public class ChatMessage implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 单聊这里固定填 1
     */
    private Integer receiverType;

    /**
     * 文本消息内容
     */
    private String msg;

    /**
     * 消息类型：1=文本，2=图片，3=视频，4=语音等
     */
    private Integer msgType;

    /**
     * 消息时间（发送/接收时间）
     */
    private LocalDateTime chatTime;

    // ===== 视频相关 =====
    private String videoPath;
    private Integer videoWidth;
    private Integer videoHeight;
    private Integer videoTimes;

    // ===== 语音相关 =====
    private String voicePath;
    private Integer speakVoiceDuration;

    /**
     * 是否已读：0=未读，1=已读
     */
    private Integer isRead;

    /**
     * 是否删除：0=否，1=是
     */
    private Integer isDelete;
}
