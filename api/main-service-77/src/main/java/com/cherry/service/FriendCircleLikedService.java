package com.cherry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.pojo.FriendCircleLiked;
import com.cherry.vo.TinyUserVO;

import java.util.List;

/**
 * <p>
 * 点赞朋友圈的朋友 服务类
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
public interface FriendCircleLikedService extends IService<FriendCircleLiked> {

    /**
     * 点赞或者取消点赞朋友圈
     * @param userId
     * @param circleId
     */
    boolean likeOrUnlike(Long userId, Long circleId);

    /**
     * 统计朋友圈点赞数量
     * @param circleId
     * @return
     */
    long countLike(Long circleId);

    /**
     * 查看当前用户是否点赞
     * @param circleId
     * @param loginUserId
     * @return
     */
    boolean isLiked(Long circleId, Long loginUserId);

    /**
     * 得到给当前这条朋友圈点赞的用户.
     * @param circleId
     * @return
     */
    List<TinyUserVO> getLikedUsers(Long circleId);
}
