package com.cherry.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendRequestVo {
    private Long id;            // 好友申请记录 id
    private Long myId;          // 我的 id（被申请人）
    private Long friendId;      // 对方 id（申请人）
    private String verifyMsg;   // 附言
    private Integer status;     // 0待处理 1同意 2拒绝
    private LocalDateTime createTime;

    // 关联的“申请人”的用户信息
    private UserVo friendUser;  // 这里用你已经写好的 UserVo
}
