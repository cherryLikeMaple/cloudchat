package com.cherry.dto.user;

import com.cherry.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author cherry
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UsersQueryRequest extends PageRequest implements Serializable {
    
    
    private Long id;

    /**
     * 登录账号（唯一）
     */
    private String account;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别，1:男 0:女 2:保密
     */
    private Integer sex;

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
     * 用户角色：user/admin/ban
     */
    private String userRole;

}
