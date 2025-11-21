package com.cherry.protocol.dto;

import lombok.Data;

/**
 * @author cherry
 */
@Data
public class HeartReq {

    private String type;      // "HEART"
    private Long clientTime;
}
