package com.cherry.service;

import com.cherry.dto.ws.WsChatSendReq;
import com.cherry.vo.WsChatMsgVO;

/**
 * <p>
 * 聊天信息存储表 服务类
 * </p>
 *
 * @author cherry
 * @since 2025-11-19
 */
public interface ChatMessageService{


    /**
     * 发送单聊消息（核心业务）
     * @param senderId  当前登录用户（从 WsSession 里拿）
     * @param req       前端发来的消息 DTO
     * @return          构造好的 VO（用于推送给前端）
     */
    WsChatMsgVO sendSingleMessage(Long senderId, WsChatSendReq req);

    
    
}
