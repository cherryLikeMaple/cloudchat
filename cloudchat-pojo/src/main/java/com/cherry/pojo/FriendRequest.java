package com.cherry.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 好友请求记录表
 * </p>
 *
 * @author cherry
 * @since 2025-11-15
 */
@TableName("friend_request")
@Data
public class FriendRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 添加好友，发起请求的用户id
     */
    private Long myId;

    /**
     * 要添加的朋友的id
     */
    private Long friendId;

    /**
     * 好友的备注名
     */
    private String friendRemark;

    /**
     * 请求的留言，验证消息
     */
    private String verifyMessage;

    /**
     * 请求被好友审核的状态，0-待审核；1-已添加，2-已过期
     */
    private Integer verifyStatus;

    /**
     * 创建时间
     */
    private LocalDateTime requestTime;
    
}
