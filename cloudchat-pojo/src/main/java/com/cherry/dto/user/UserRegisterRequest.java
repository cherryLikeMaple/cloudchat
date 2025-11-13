package com.cherry.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cherry
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;
    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
