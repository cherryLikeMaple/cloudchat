package com.cherry.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author cherry
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户头像
     */
    private String face;
    /**
     * 性别，1:男 0:女 2:保密
     */
    private Integer sex;
    /**
     * 用户昵称.
     */
    private String nickname;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 生日
     */
    private LocalDate birthday;
    /**
     * 国家
     */
    private String country;
    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;
    /**
     * 我的一句话签名
     */
    private String signature;
}
