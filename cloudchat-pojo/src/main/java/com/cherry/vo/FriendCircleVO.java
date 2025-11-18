package com.cherry.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 朋友圈展示 VO
 *
 * @author cherry
 */
@Data
public class FriendCircleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 朋友圈 id
     */
    private Long id;

    /**
     * 发布者 id
     */
    private Long userId;

    /**
     * 发布者昵称
     */
    private String nickname;

    /**
     * 发布者头像
     */
    private String face;

    /**
     * 文字内容
     */
    private String words;

    /**
     * 图片内容（多图，分割后返回列表）
     */
    private List<String> imageList;

    /**
     * 视频 url
     */
    private String video;

    /**
     * 发布时间
     */
    private LocalDateTime createTime;

    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 点赞的用户数量
     */
    private List<CircleUserVO> likedUserList;

    /**
     * 当前登录用户是否已点赞
     */
    private boolean liked;
}
