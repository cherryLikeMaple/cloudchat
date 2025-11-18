package com.cherry.dto.friendCycle;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 发布朋友圈 DTO
 * 前端 -> 后端
 * @author cherry
 */
@Data
public class CreateFriendCircleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文字内容（可为空）
     */
    private String words;

    /**
     * 图片列表，前端传数组，例如：["url1", "url2"]
     */
    private List<String> images;

    /**
     * 视频 url（可为空）
     */
    private String video;
}
