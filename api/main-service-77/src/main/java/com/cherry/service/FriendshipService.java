package com.cherry.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.enums.YesOrNo;
import com.cherry.pojo.Friendship;
import com.cherry.vo.FriendshipVo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * 朋友关系表 服务类
 * </p>
 *
 * @author cherry
 * @since 2025-11-15
 */
public interface FriendshipService extends IService<Friendship> {

    /**
     * 得到好友关系
     * @param friendId
     * @param myId
     * @return
     */
    FriendshipVo getFriendShipVo(Long friendId, Long myId);
    FriendshipVo getFriendShipVo(Friendship friendShip);

    /**
     * 返回我所有好友的列表. 
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     */
    Page<FriendshipVo> getFriendShipVoPage(Long currentPage, Long pageSize, HttpServletRequest request, Integer isBlack);

    /**
     * 更新好友备注
     * @param myId
     * @param friendId
     */
    void updateFriendRemark(Long myId, Long friendId, String remark);

    /**
     * 更新黑名单状态
     * @param myId
     * @param friendId
     * @param yesOrNo
     */
    void  updateBlackList(Long myId, Long friendId, int yesOrNo);

    /**
     * 删除好友(单向)
     * @param myId
     * @param friendId
     */
    void deleteFriend(Long myId, Long friendId);

    /**
     * 判断是否在黑名单中
     * @param myId
     * @param friendId
     * @return
     */
    boolean isBlack(Long myId, Long friendId);
}
