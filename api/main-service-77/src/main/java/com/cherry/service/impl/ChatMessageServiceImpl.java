package com.cherry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.constant.RedisKeys;
import com.cherry.exceptions.GraceException;
import com.cherry.mapper.ChatMessageMapper;
import com.cherry.pojo.ChatMessage;
import com.cherry.service.ChatMessageService;
import com.cherry.ws.MsgType;
import com.cherry.ws.WsChatMsgVO;
import com.cherry.ws.WsChatSendReq;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cherry.grace.result.ResponseStatusEnum.FAILED;

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


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public ChatMessage dtoToEntity(WsChatSendReq req) {


        ChatMessage entity = new ChatMessage();

        entity.setMsgId(req.getMsgId());
        // 发送者、接收者
        entity.setSenderId(req.getSenderId());
        entity.setReceiverId(req.getReceiverId());
        entity.setChatType(req.getChatType().byteValue());

        // 消息类型
        if (req.getMsgType() != null) {
            entity.setMsgType(req.getMsgType().code);
        }

        // 文本内容
        entity.setContent(req.getContent());

        // 时间（当前时间）
        entity.setChatTime(req.getChatTime());

        // 默认状态
        entity.setIsRead(0);
        entity.setIsDelete(0);

        // 按消息类型填充多媒体相关字段
        if (req.getMsgType() == MsgType.IMAGE || req.getMsgType() == MsgType.VIDEO) {
            // 图片/视频共用字段
            entity.setMediaUrl(req.getMediaUrl());
            entity.setMediaWidth(req.getMediaWidth());
            entity.setMediaHeight(req.getMediaHeight());
        }
        if (req.getMsgType() == MsgType.VIDEO) {
            entity.setVideoCoverUrl(req.getVideoCoverUrl());
            entity.setVideoTimes(req.getVideoDuration());
        }

        if (req.getMsgType() == MsgType.VOICE) {
            entity.setVoiceUrl(req.getVoiceUrl());
            entity.setVoiceDuration(req.getVoiceDuration());
        }

        return entity;
    }

    @Override
    @Transactional
    public void saveMsg(ChatMessage chatMessage) {
        boolean result = this.save(chatMessage);
        if (!result) {
            GraceException.display(FAILED);
        }
        String key = String.format("chat:unread:%d:%d", chatMessage.getReceiverId(), chatMessage.getSenderId());
        // redis key: chat:unread:接收人
        stringRedisTemplate.opsForValue().increment(key);
    }

    @Override
    public IPage<WsChatMsgVO> listHistory(Long myId, Long friendId, long pageNum, long pageSize) {
        Page<ChatMessage> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(ChatMessage::getIsDelete, 0)
                .and(w -> w
                        .eq(ChatMessage::getSenderId, myId)
                        .eq(ChatMessage::getReceiverId, friendId)
                        .or()
                        .eq(ChatMessage::getSenderId, friendId)
                        .eq(ChatMessage::getReceiverId, myId)
                )
                .orderByDesc(ChatMessage::getChatTime);

        IPage<ChatMessage> msgPage = this.page(page, wrapper);

        Page<WsChatMsgVO> voPage = new Page<>();
        voPage.setCurrent(msgPage.getCurrent());
        voPage.setSize(msgPage.getSize());
        voPage.setTotal(msgPage.getTotal());

        List<WsChatMsgVO> voList = msgPage.getRecords().stream().map(msg -> {
            WsChatMsgVO vo = new WsChatMsgVO();
            vo.setMsgId(msg.getMsgId());
            vo.setChatType(msg.getChatType());
            vo.setSenderId(msg.getSenderId());
            vo.setReceiverId(msg.getReceiverId());
            vo.setContent(msg.getContent());
            vo.setMsgType(msg.getMsgType());
            vo.setChatTime(msg.getChatTime());
            vo.setIsRead(msg.getIsRead());

            if (msg.getMediaUrl() != null) {
                vo.setMediaUrl(msg.getMediaUrl());
            }
            if (msg.getMediaWidth() != null) {
                vo.setMediaWidth(msg.getMediaWidth());
            }
            if (msg.getMediaHeight() != null) {
                vo.setMediaHeight(msg.getMediaHeight());
            }
            if (msg.getVideoCoverUrl() != null) {
                vo.setVideoCoverUrl(msg.getVideoCoverUrl());
            }
            if (msg.getVideoTimes() != null) {
                vo.setVideoTimes(msg.getVideoTimes());
            }
            if (msg.getVoiceUrl() != null) {
                vo.setVoiceUrl(msg.getVoiceUrl());
            }
            if (msg.getVoiceDuration() != null) {
                vo.setVoiceDuration(msg.getVoiceDuration());
            }

            return vo;
        }).toList();
        voPage.setRecords(voList);

        return voPage;
    }

}
