package com.cherry.dto.user;

import lombok.Data;

/**
 * @author cherry
 */
@Data
public class AddUsersRequest {
    
    private String myId;
    private String friendId;
    private String verifyMessage;
    private String friendRemark;
}
