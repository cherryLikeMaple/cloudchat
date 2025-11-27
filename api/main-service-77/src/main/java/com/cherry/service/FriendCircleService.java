package com.cherry.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.dto.friendCycle.CreateFriendCircleDTO;
import com.cherry.enums.YesOrNo;
import com.cherry.pojo.FriendCircle;
import com.cherry.vo.FriendCircleVO;
import com.cherry.vo.FriendshipVo;

/**
 * <p>
 * 朋友圈表 服务类
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
public interface FriendCircleService extends IService<FriendCircle> {

    /**
     * 发布朋友圈
     *
     * @param createFriendCircleDTO
     */
    void publish(CreateFriendCircleDTO createFriendCircleDTO, Long userId);

    /**
     * 查询某一位好友的朋友圈
     *
     * @param friendId
     * @param currentPage
     * @param pageSize
     * @return
     */
    Page<FriendCircleVO> listFriendCircleByUser(Long loginUserId, Long friendId, Integer currentPage, Integer pageSize);

    /**
     * 查看我的所有好友的朋友圈
     *
     * @param loginUserId
     * @param currentPage
     * @param pageSize
     * @return
     */
    Page<FriendCircleVO> listAllFriendCircle(Long loginUserId, Integer currentPage, Integer pageSize);

    /**
     * 返回单个朋友圈信息
     * @param circleId
     * @return
     */
    FriendCircleVO getFriendCircle(Long circleId, Long loginUserId);

    /**
     * 统一设置点赞相关的参数
     * @param circleId
     * @param loginUserId
     * @param friendCircleVO
     */
    void batchSetCircleLiked(Long circleId, Long loginUserId, FriendCircleVO friendCircleVO);
    
    
}
