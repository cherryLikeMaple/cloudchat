package com.cherry.service.impl;

import com.cherry.protocol.enums.ChatType;
import com.cherry.protocol.dto.WsChatSendReq;
import com.cherry.pojo.ChatMessage;
import com.cherry.service.ChatMessageService;
import com.cherry.protocol.vo.WsChatMsgVO;
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
public class ChatMessageServiceImpl implements ChatMessageService {


    @Override
    public WsChatMsgVO sendSingleMessage(Long senderId, WsChatSendReq req) {
        // ===== 1. 简单校验 =====
        if (req.getChatType() == null || req.getChatType() != 1) {
            throw new IllegalArgumentException("当前方法只支持单聊 chatType = 1");
        }
        if (req.getReceiverId() == null) {
            throw new IllegalArgumentException("receiverId 不能为空");
        }
        if (req.getMsgType() == null) {
            throw new IllegalArgumentException("msgType 不能为空");
        }

        // ===== 2. 封装实体写库 =====
        ChatMessage entity = new ChatMessage();
        entity.setSenderId(senderId);
        entity.setReceiverId(req.getReceiverId());
        entity.setReceiverType(ChatType.SINGLE.code);
        entity.setMsgType(req.getMsgType().code);
        entity.setChatTime(LocalDateTime.now());
        entity.setIsRead(0);
        entity.setIsDelete(0);

        switch (req.getMsgType()) {
            case TEXT:
                entity.setMsg(req.getContent());
                break;
            case IMAGE:
                // 方案1：把图片 url 写在 msg 字段
                entity.setMsg(req.getMediaUrl());
                // 如果你想存宽高，可以占用 videoWidth/videoHeight
                entity.setVideoWidth(req.getMediaWidth());
                entity.setVideoHeight(req.getMediaHeight());
                break;
            case VIDEO:
                entity.setVideoPath(req.getMediaUrl());
                entity.setVideoWidth(req.getMediaWidth());
                entity.setVideoHeight(req.getMediaHeight());
                entity.setVideoTimes(req.getMediaDuration());
                break;
            case VOICE:
                entity.setVoicePath(req.getVoiceUrl());
                entity.setSpeakVoiceDuration(req.getVoiceDuration());
                break;
            default:
                throw new IllegalArgumentException("不支持的 msgType：" + req.getMsgType());
        }

        WsChatMsgVO vo = new WsChatMsgVO();
        vo.setId(entity.getId());
        vo.setClientMsgId(req.getMsgId());
        vo.setChatType(req.getChatType());
        vo.setSenderId(senderId);
        vo.setReceiverId(req.getReceiverId());
        vo.setReceiverType(entity.getReceiverType());
        vo.setMsgType(entity.getMsgType());
        vo.setMsg(entity.getMsg());
        vo.setVideoPath(entity.getVideoPath());
        vo.setVideoWidth(entity.getVideoWidth());
        vo.setVideoHeight(entity.getVideoHeight());
        vo.setVideoTimes(entity.getVideoTimes());
        vo.setVoicePath(entity.getVoicePath());
        vo.setSpeakVoiceDuration(entity.getSpeakVoiceDuration());
        vo.setChatTime(entity.getChatTime());
        vo.setIsRead(entity.getIsRead());

        return vo;
    }

}
