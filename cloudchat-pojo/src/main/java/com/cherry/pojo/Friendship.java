package com.cherry.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 朋友关系表
 * </p>
 *
 * @author cherry
 * @since 2025-11-15
 */
@Data
public class Friendship implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 自己的用户id
     */
    private Long myId;

    /**
     * 我朋友的id
     */
    private Long friendId;

    /**
     * 好友的备注名
     */
    private String friendRemark;

    /**
     * 是否消息免打扰，0-打扰，不忽略消息(默认)；1-免打扰，忽略消息
     */
    private Integer isMsgIgnore;

    /**
     * 是否拉黑，0-好友(默认)；1-已拉黑
     */
    private Integer isBlack;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    

}
