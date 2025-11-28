package com.cherry.enums;

import lombok.Getter;

/**
 * 朋友圈可见范围枚举
 *
 * 0 = 仅自己可见
 * 1 = 好友可见
 * 2 = 所有人可见
 *
 * @author cherry
 */
@Getter
public enum VisibleScopeEnum {

    SELF(0, "仅自己可见"),
    FRIEND(1, "好友可见"),
    PUBLIC(2, "所有人可见");

    /**
     * 编码值（入库字段）
     */
    private final Integer code;

    /**
     * 描述（业务文案）
     */
    private final String desc;

    VisibleScopeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据 code 获取枚举
     *
     * @param code 入库的 tinyint 字段值
     * @return VisibleScopeEnum
     */
    public static VisibleScopeEnum fromCode(Integer code) {
        if (code == null) {
            return FRIEND; // 默认好友可见
        }
        for (VisibleScopeEnum item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return FRIEND; // 防御性兜底
    }
}
