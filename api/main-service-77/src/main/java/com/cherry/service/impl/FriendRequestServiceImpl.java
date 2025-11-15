package com.cherry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.controller.FriendRequestController;
import com.cherry.dto.user.AddUsersRequest;
import com.cherry.enums.FriendRequestVerifyStatus;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.mapper.FriendRequestMapper;
import com.cherry.pojo.FriendRequest;
import com.cherry.pojo.Users;
import com.cherry.service.FriendRequestService;
import com.cherry.service.UsersService;
import com.cherry.vo.FriendRequestVo;
import com.cherry.vo.UserVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 好友请求记录表 服务实现类
 * </p>
 *
 * @author cherry
 * @since 2025-11-15
 */
@Service
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestService {

    @Resource
    private FriendRequestMapper friendRequestMapper;
    @Resource
    private UsersService usersService;

    @Override
    public void validFriendRequest(FriendRequest friendRequest) {
        if (friendRequest == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        String friendRemark = friendRequest.getFriendRemark();
        String verifyMessage = friendRequest.getVerifyMessage();
        if (StringUtils.isBlank(friendRemark)) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        if (StringUtils.isBlank(verifyMessage)) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
    }

    @Override
    public void addFriendRequest(AddUsersRequest addUsersRequest) {
        // 先删除以前的记录
        QueryWrapper<FriendRequest> friendRequestQueryWrapper = new QueryWrapper<>();
        friendRequestQueryWrapper.eq("friend_id", addUsersRequest.getFriendId())
                .eq("my_id", addUsersRequest.getMyId());
        friendRequestMapper.delete(friendRequestQueryWrapper);

        // 再新增记录
        FriendRequest friendRequest = new FriendRequest();
        BeanUtils.copyProperties(addUsersRequest, friendRequest);
        friendRequest.setVerifyStatus(FriendRequestVerifyStatus.WAIT.type);

        friendRequestMapper.insert(friendRequest);
    }

    @Override
    public Page<FriendRequestVo> getFriendRequestVoPage(Long currentPage,
                                                        Long pageSize,
                                                        HttpServletRequest request) {
        // 1. 当前登录用户
        Users loginUser = usersService.getLoginUser(request);
        Long myId = loginUser.getId();

        // 2. 构造分页参数
        Page<FriendRequest> page = new Page<>(currentPage, pageSize);

        // 3. 查“我收到的好友申请分页”
        LambdaQueryWrapper<FriendRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FriendRequest::getMyId, myId)
                .orderByDesc(FriendRequest::getRequestTime);

        Page<FriendRequest> reqPage = this.page(page, wrapper);
        List<FriendRequest> friendRequests = reqPage.getRecords();
        if (friendRequests.isEmpty()) {
            Page<FriendRequestVo> empty = new Page<>(reqPage.getCurrent(), reqPage.getSize(), reqPage.getTotal());
            empty.setRecords(Collections.emptyList());
            return empty;
        }

        // 4. 收集所有“对方用户 id”（申请人）
        Set<Long> friendIds = friendRequests.stream()
                .map(FriendRequest::getFriendId)
                .collect(Collectors.toSet());

        // 5. 批量查用户信息
        List<Users> users = usersService.listByIds(friendIds);

        Map<Long, UserVo> userVoMap = users.stream()
                .collect(Collectors.toMap(
                        Users::getId,
                        usersService::getUserVo
                ));

        // 6. 组装 FriendRequestVo 列表
        List<FriendRequestVo> voList = friendRequests.stream()
                .map(req -> {
                    FriendRequestVo vo = new FriendRequestVo();
                    BeanUtils.copyProperties(req, vo);

                    UserVo friendUserVo = userVoMap.get(req.getFriendId());
                    vo.setFriendUser(friendUserVo);

                    return vo;
                })
                .collect(Collectors.toList());

        // 7. 构造 Page<FriendRequestVo> 返回
        Page<FriendRequestVo> voPage = new Page<>(reqPage.getCurrent(), reqPage.getSize(), reqPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

}
