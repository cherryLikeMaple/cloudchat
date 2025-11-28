package com.cherry.dist;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.session.WsChannelManager;
import com.cherry.utils.OkHttpUtil;
import com.cherry.ws.MsgType;
import com.cherry.ws.WsChatMsgVO;
import com.cherry.ws.WsChatSendReq;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author cherry
 */
@Component
public class ManageUtils {

    @Value("${my.base-url}")
    private String baseUrl;

    public WsChatMsgVO dtoToVo(WsChatSendReq req) {

        WsChatMsgVO vo = new WsChatMsgVO();

        // === 必要字段 ===
        vo.setMsgId(req.getMsgId());
        vo.setChatType(req.getChatType());
        vo.setSenderId(req.getSenderId());
        vo.setReceiverId(req.getReceiverId());
        vo.setChatType(req.getChatType());

        // === 消息类型 ===
        if (req.getMsgType() != null) {
            vo.setMsgType(req.getMsgType().code);
        }

        // === 文本内容 ===
        vo.setContent(req.getContent());

        // === 时间（最好用服务器时间）===
        vo.setChatTime(LocalDateTime.now());

        // === 默认未读 ===
        vo.setIsRead(0);

        // === 图片 or 视频 ===
        if (req.getMsgType() == MsgType.IMAGE || req.getMsgType() == MsgType.VIDEO) {
            vo.setMediaUrl(req.getMediaUrl());
            vo.setMediaWidth(req.getMediaWidth());
            vo.setMediaHeight(req.getMediaHeight());
        }

        // === 视频特有字段 ===
        if (req.getMsgType() == MsgType.VIDEO) {
            vo.setMediaUrl(req.getMediaUrl());
            vo.setVideoTimes(req.getVideoDuration());
        }

        // === 语音字段 ===
        if (req.getMsgType() == MsgType.VOICE) {
            vo.setVoiceUrl(req.getVoiceUrl());
            vo.setVoiceDuration(req.getVoiceDuration());
        }

        return vo;
    }

    /**
     * 处理文本消息的“单发单聊”
     */
    public void send(WsChatSendReq req, Long senderId, Channel channel) {
        // 判断是否为拉黑好友.
        // note 当前体系不是spring cloud, 所以可以是要http工具包. 直接给本地服务发送请求.
        GraceJSONResult jsonResult = OkHttpUtil.get(baseUrl + "/friendShip/isBlack?myUserId=" + senderId + "&friendId=" + req.getReceiverId());
        boolean isBlack = (boolean) jsonResult.getData();
        if (isBlack) {
            channel.writeAndFlush(new TextWebSocketFrame("对方已将你拉黑或你已经把对方拉黑，无法发送消息"));
            return;
        }

        WsChatMsgVO vo;
        try {
            vo = this.dtoToVo(req);
        } catch (Exception e) {
            e.printStackTrace();
            channel.writeAndFlush(new TextWebSocketFrame("消息发送失败，请稍后重试"));
            return;
        }
        // 原先使用 hutool 转 json的方法, 但是导致精度丢失
//        String voJson = JSONUtil.toJsonStr(vo);
        
        // 替换为fastjson2 
        String voJson = JSON.toJSONString(vo,
                JSONWriter.Feature.WriteLongAsString,
                JSONWriter.Feature.WriteNulls);
        
        Long receiverId = vo.getReceiverId();
        

        // 推给对方所有端
        WsChannelManager.sendToUser(receiverId, voJson);
        // 推给自己其它端
        WsChannelManager.sendToUserExceptChannel(senderId, channel, voJson);
        // 推给当前端，作为“发送成功”的依据
        channel.writeAndFlush(new TextWebSocketFrame(voJson));
    }
}
