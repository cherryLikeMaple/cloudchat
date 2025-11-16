package com.cherry.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 好友展示信息（返回给前端）
 * @author cherry
 */
@Data
public class FriendshipVo {

    /**
     * 好友关系 ID（可选）
     */
    private Long id;

    /**
     * 好友用户信息
     */
    private UserVo friendUser;

    /**
     * 自定义备注
     */
    private String friendRemark;

    /**
     * 消息免打扰 0/1
     */
    private Integer isMsgIgnore;

    /**
     * 拉黑状态 0/1
     */
    private Integer isBlack;

    /**
     * 是否在线（可选，看你项目是否有）
     */
    private Boolean isOnline;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
