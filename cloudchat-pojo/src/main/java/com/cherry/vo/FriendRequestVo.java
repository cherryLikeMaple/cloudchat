package com.cherry.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendRequestVo {
    private Long id;
    /**
     * 申请人
     */
    private Long myId;
    /**
     * 被加的人.
     */
    private Long friendId;      
    private String verifyMsg;   
    private Integer verifyStatus;     // 0待处理 1同意 2拒绝
    private LocalDateTime createTime;

    // 关联的“申请人”的用户信息
    private UserVo friendUser; 
    
    private UserVo my;
}
