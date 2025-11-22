package com.cherry.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cherry.api.feign.UserInfoMicroServiceFeign;
import com.cherry.constant.RedisKeys;
import com.cherry.dto.friendcCircleComment.CommentAddRequest;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.pojo.Users;
import com.cherry.service.ChatMessageService;
import com.cherry.service.CommentService;
import com.cherry.vo.CommentVO;
import com.cherry.ws.WsChatMsgVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 朋友圈表 前端控制器
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ChatMessageService chatMessageService;

    /**
     * 设置未读消息的量
     *
     * @param request
     * @param senderId
     * @return
     */
    @GetMapping("/getMyUnReadCounts")
    public GraceJSONResult getMyUnReadCounts(HttpServletRequest request, Long senderId) {
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        String key = String.format("chat:unread:%d:%d", loginUser.getId(), senderId);
        return GraceJSONResult.ok(stringRedisTemplate.opsForValue().get(key));
    }

    @PostMapping("/clearMyUnReadCounts")
    public GraceJSONResult clearMyUnReadCounts(HttpServletRequest request, Long senderId) {
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        String key = String.format("chat:unread:%d:%d", loginUser.getId(), senderId);
        stringRedisTemplate.delete(key);
        return GraceJSONResult.ok();
    }
    
    @GetMapping("/getHistoryMsg")
    public GraceJSONResult history(@RequestParam Long friendId,
                                   @RequestParam(defaultValue = "1") Long pageNum,
                                   @RequestParam(defaultValue = "20") Long pageSize,
                                   HttpServletRequest request) {
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long myId = loginUser.getId();

        IPage<WsChatMsgVO> wsChatMsgVOIPage = chatMessageService.listHistory(myId, friendId, pageNum, pageSize);
        
        return GraceJSONResult.ok(wsChatMsgVOIPage);
    }
}
