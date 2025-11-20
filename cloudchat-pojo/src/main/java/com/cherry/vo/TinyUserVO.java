package com.cherry.vo;

import com.cherry.pojo.Users;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cherry
 */
@Data
public class TinyUserVO implements Serializable {

    private Long userId;
    private String nickname;
    private String face;

    // note 使用静态工厂方法, 调用更加干净.
    public static TinyUserVO fromEntity(Users users) {
        if (users == null) {
            return null;
        }
        TinyUserVO vo = new TinyUserVO();
        vo.setUserId(users.getId());
        vo.setNickname(users.getNickname());
        vo.setFace(users.getFace());
        return vo;
    }
}
