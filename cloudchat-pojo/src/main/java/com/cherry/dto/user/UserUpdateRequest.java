package com.cherry.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cherry
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;
    
    private String userId;
    private String face;
    private Integer sex;
    private String nickname;
    private String mobile;
    private String province;
    private String city;
    private String signature;
}
