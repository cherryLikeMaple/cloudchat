package com.cherry.dto.user;

import lombok.Data;

/**
 * @author cherry
 */
@Data
public class AddUsersRequest {

    /**
     * 申请人.
     */
    private Long myId;
    /**
     * 被加的人
     */
    private Long friendId;
    private String verifyMessage;
    private String friendRemark;
}
