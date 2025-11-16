package com.cherry.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.dto.user.AddUsersRequest;
import com.cherry.pojo.FriendRequest;
import com.cherry.vo.FriendRequestVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

/**
 * <p>
 * 好友请求记录表 服务类
 * </p>
 *
 * @author cherry
 * @since 2025-11-15
 */
public interface FriendRequestService extends IService<FriendRequest> {

    
    void validFriendRequest(FriendRequest friendRequest);

    /**
     * 添加好友
     * @param addUsersRequest
     */
    void addFriendRequest(AddUsersRequest addUsersRequest);

    /**
     * 查看请求的好友列表
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     */
    Page<FriendRequestVo> getFriendRequestVoPage(Long currentPage,
                                                 Long pageSize,
                                                 HttpServletRequest request);


    /**
     * 同意好友请求
     * @param friendRequestId
     * @param friendRemark
     */
    void passFriendRequest(String friendRequestId, String friendRemark);

    /**
     * 拒绝好友请求
     * @param friendRequestId
     */
    void refuseFriendRequest(String friendRequestId);

    /**
     * 得到包装类
     * @param friendRequest
     * @return
     */
    FriendRequestVo getFriendRequestVo(FriendRequest friendRequest);

}
