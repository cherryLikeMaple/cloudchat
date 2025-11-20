package com.cherry.netty.websocket;

import cn.hutool.json.JSONUtil;
import com.cherry.dto.ws.MsgType;
import com.cherry.dto.ws.WsChatSendReq;
import com.cherry.netty.websocket.manage.WsChannelManager;
import com.cherry.netty.websocket.manage.WsSession;
import com.cherry.service.ChatMessageService;
import com.cherry.vo.WsChatMsgVO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author cherry
 */
@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    @Resource
    private ChatMessageService chatMessageService;

    /**
     * 断开连接时候
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        WsChannelManager.removeSessionByChannel(channel);
        super.handlerRemoved(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();
        String text = msg.text();
        System.out.println("收到原始消息 = " + text);

        // 1.鉴权
        WsSession session = WsChannelManager.getSession(channel);
        if (session == null) {
            channel.writeAndFlush(new TextWebSocketFrame("未授权连接，拒绝处理消息"));
            return;
        }

        Long senderId = session.getUserId();

        // 2. 解析消息
        WsChatSendReq req;
        try {
            req = JSONUtil.toBean(text, WsChatSendReq.class);
        } catch (Exception e) {
            e.printStackTrace();
            channel.writeAndFlush(new TextWebSocketFrame("消息格式错误，请重试"));
            return;
        }

        // 4. 根据 msgType 分发
        MsgType msgType = req.getMsgType();
        if (msgType == null) {
            channel.writeAndFlush(new TextWebSocketFrame("msgType 不能为空"));
            return;
        }

        switch (msgType) {
            case TEXT:
                handleText(req, senderId, channel);
                break;
                case IMAGE:
                // TODO: 实现图片消息
                channel.writeAndFlush(new TextWebSocketFrame("图片消息暂未实现"));
                break;
            case VIDEO:
                // TODO: 实现视频消息
                channel.writeAndFlush(new TextWebSocketFrame("视频消息暂未实现"));
                break;
            case VOICE:
                // TODO: 实现语音消息
                channel.writeAndFlush(new TextWebSocketFrame("语音消息暂未实现"));
                break;
            default:
                channel.writeAndFlush(new TextWebSocketFrame("不支持的消息类型：" + msgType));
        }
    }

    /**
     * 处理文本消息的“单发单聊”
     */
    private void handleText(WsChatSendReq req, Long senderId, Channel channel) {
        WsChatMsgVO vo;
        try {
            vo = chatMessageService.sendSingleMessage(senderId, req);
        } catch (Exception e) {
            e.printStackTrace();
            channel.writeAndFlush(new TextWebSocketFrame("消息发送失败，请稍后重试"));
            return;
        }

        String voJson = JSONUtil.toJsonStr(vo);
        Long receiverId = vo.getReceiverId();

        // 推给对方所有端
        WsChannelManager.sendToUser(receiverId, voJson);
        // 推给自己其它端
        WsChannelManager.sendToUserExceptChannel(senderId, channel, voJson);
        // 推给当前端，作为“发送成功”的依据
        channel.writeAndFlush(new TextWebSocketFrame(voJson));
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("异常：" + cause.getMessage());
        ctx.close();
        // netty会自动处理断开的channel.
    }
}
