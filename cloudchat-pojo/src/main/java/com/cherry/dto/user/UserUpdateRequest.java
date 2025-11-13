package com.cherry.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;
    
    private String userId;
    private String face;
    private Integer sex;
    private String nickname;
    private String wechatNum;
    private String province;
    private String city;
    private String district;
    private String chatBg;
    private String friendCircleBg;
    private String signature;
}
