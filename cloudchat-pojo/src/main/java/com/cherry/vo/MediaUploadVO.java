package com.cherry.vo;

import lombok.Data;

/**
 * @author cherry
 */
@Data
public class MediaUploadVO {


    private String mediaUrl;
    private int width;
    private int height;

    /**
     * 视频封面地址.
     */
    private String coverUrl;
    private int duration;
    
}
