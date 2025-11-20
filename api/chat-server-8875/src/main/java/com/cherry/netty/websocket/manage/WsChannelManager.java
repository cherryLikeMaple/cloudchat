package com.cherry.netty.websocket.manage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author cherry
 */
public class WsChannelManager {

    /**
     * channelId -> session
     */
    private static final ConcurrentMap<ChannelId, WsSession> CHANNEL_SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * userId -> (uniqueKey -> session)
     */
    private static final ConcurrentMap<Long, ConcurrentMap<String, WsSession>> USER_SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 绑定会话（鉴权成功后调用）
     */
    public static void bindSession(Channel channel,
                                   Long userId,
                                   String clientType,
                                   String deviceId,
                                   String token) {
        WsSession session = new WsSession();
        session.setUserId(userId);
        session.setClientType(clientType);
        session.setDeviceId(deviceId);
        session.setToken(token);
        session.setChannel(channel);

        CHANNEL_SESSION_MAP.put(channel.id(), session);

        USER_SESSION_MAP
                .computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                .put(session.uniqueKey(), session);
    }

    /**
     * 通过 channel 删除会话（断开连接时调用）
     */
    public static void removeSessionByChannel(Channel channel) {
        ChannelId channelId = channel.id();
        WsSession session = CHANNEL_SESSION_MAP.remove(channelId);
        if (session == null) {
            return;
        }
        Long userId = session.getUserId();
        ConcurrentMap<String, WsSession> map = USER_SESSION_MAP.get(userId);
        if (map != null) {
            map.remove(session.uniqueKey());
            if (map.isEmpty()) {
                USER_SESSION_MAP.remove(userId);
            }
        }
    }

    /**
     * 通过 channel 获取当前会话
     */
    public static WsSession getSession(Channel channel) {
        if (channel == null) {
            return null;
        }
        return CHANNEL_SESSION_MAP.get(channel.id());
    }

    /**
     * 获取某个用户的所有在线端
     */
    public static Collection<WsSession> getUserSessions(Long userId) {
        ConcurrentMap<String, WsSession> map = USER_SESSION_MAP.get(userId);
        if (map == null) {
            return Collections.emptyList();
        }
        return map.values();
    }

    /**
     * 返回连接数量
     * @return
     */
    public static int getSessionSize() {
        return USER_SESSION_MAP.size();
    }

    /**
     * 给某个用户所有端发消息
     */
    public static void sendToUser(Long userId, String json) {
        for (WsSession session : getUserSessions(userId)) {
            Channel ch = session.getChannel();
            if (ch.isActive()) {
                ch.writeAndFlush(new TextWebSocketFrame(json));
            }
        }
    }

    /**
     * 给某个用户所有端发消息（排除当前这个 channel）
     *   - 比如自己在 web 端发了一条，想同步给自己手机/平板，但没必要再推给当前 web 端
     */
    public static void sendToUserExceptChannel(Long userId, Channel excludeChannel, String json) {
        for (WsSession session : getUserSessions(userId)) {
            Channel ch = session.getChannel();
            if (ch == excludeChannel) {
                continue;
            }
            if (ch.isActive()) {
                ch.writeAndFlush(new TextWebSocketFrame(json));
            }
        }
    }
}
