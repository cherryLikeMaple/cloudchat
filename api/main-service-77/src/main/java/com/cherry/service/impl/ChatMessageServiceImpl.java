package com.cherry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.ws.MsgType;
import com.cherry.ws.WsChatSendReq;
import com.cherry.mapper.ChatMessageMapper;
import com.cherry.pojo.ChatMessage;
import com.cherry.service.ChatMessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 聊天信息存储表 服务实现类
 * </p>
 *
 * @author cherry
 * @since 2025-11-19
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {


    @Override
    public ChatMessage dtoToEntity(WsChatSendReq req) {


        ChatMessage entity = new ChatMessage();

        entity.setMsgId(req.getMsgId());
        // 发送者、接收者
        entity.setSenderId(req.getSenderId());
        entity.setReceiverId(req.getReceiverId());
        entity.setReceiverType(req.getChatType());

        // 消息类型
        if (req.getMsgType() != null) {
            entity.setMsgType(req.getMsgType().code);
        }

        // 文本内容
        entity.setMsg(req.getContent());

        // 时间（当前时间）
        entity.setChatTime(req.getSendTime());

        // 默认状态
        entity.setIsRead(0);
        entity.setIsDelete(0);

        // 按消息类型填充多媒体相关字段
        if (req.getMsgType() == MsgType.IMAGE || req.getMsgType() == MsgType.VIDEO) {
            // 图片/视频共用字段
            entity.setVideoPath(req.getMediaUrl());
            entity.setVideoWidth(req.getMediaWidth());
            entity.setVideoHeight(req.getMediaHeight());
        }
        if (req.getMsgType() == MsgType.VIDEO) {
            entity.setVideoCoverPath(req.getVideoCoverPath());
            entity.setVideoTimes(req.getMediaDuration());
        }

        if (req.getMsgType() == MsgType.VOICE) {
            entity.setVoicePath(req.getVoiceUrl());
            entity.setSpeakVoiceDuration(req.getVoiceDuration());
        }

        return entity;
    }
}
