package com.cherry.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.ws.WsChatMsgVO;
import com.cherry.ws.WsChatSendReq;
import com.cherry.pojo.ChatMessage;

/**
 * <p>
 * 聊天信息存储表 服务类
 * </p>
 *
 * @author cherry
 * @since 2025-11-19
 */
public interface ChatMessageService extends IService<ChatMessage> {


    /**
     * 前端dto, 转为entity
     * @param req
     * @return
     */
    ChatMessage dtoToEntity(WsChatSendReq req);

    /**
     * 保存信息, 同时累加未读消息记录
     * @param chatMessage
     */
    void saveMsg(ChatMessage chatMessage);
    
    IPage<WsChatMsgVO> listHistory(Long myId, Long friendId, long pageNum, long pageSize);
}
