package com.cherry.dto.user;

import lombok.Data;

/**
 * @author cherry
 */
@Data
public class AddUsersRequest {
    
    private Long myId;
    private Long friendId;
    private String verifyMessage;
    private String friendRemark;
}
