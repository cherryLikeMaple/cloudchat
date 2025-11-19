package com.cherry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.api.feign.UserInfoMicroServiceFeign;
import com.cherry.dto.user.AddUsersRequest;
import com.cherry.enums.FriendRequestVerifyStatus;
import com.cherry.enums.YesOrNo;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.mapper.FriendRequestMapper;
import com.cherry.mapper.FriendshipMapper;
import com.cherry.pojo.FriendRequest;
import com.cherry.pojo.Friendship;
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
import org.springframework.transaction.annotation.Transactional;

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
    @Resource
    private FriendshipMapper friendshipMapper;
    @Resource
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;

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
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long myId = loginUser.getId(); 

        // 2. 构造分页参数
        Page<FriendRequest> page = new Page<>(currentPage, pageSize);

        // 3. 查“我收到的好友申请（别人加我）”：friend_id = 我
        LambdaQueryWrapper<FriendRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FriendRequest::getFriendId, myId)
                .orderByDesc(FriendRequest::getRequestTime);

        Page<FriendRequest> reqPage = this.page(page, wrapper);
        List<FriendRequest> friendRequests = reqPage.getRecords();
        if (friendRequests.isEmpty()) {
            Page<FriendRequestVo> empty = new Page<>(reqPage.getCurrent(), reqPage.getSize(), reqPage.getTotal());
            empty.setRecords(Collections.emptyList());
            return empty;
        }

        // 4. 收集所有“申请人 id”（my_id 才是申请人）
        Set<Long> applicantIds = friendRequests.stream()
                .map(FriendRequest::getMyId)  
                .collect(Collectors.toSet());

        // 5. 批量查用户信息（申请人的用户信息）
        List<Users> users = usersService.listByIds(applicantIds);

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

                    // 申请人信息
                    UserVo applicantUser = userVoMap.get(req.getMyId());
                    vo.setFriendUser(applicantUser);

                    return vo;
                })
                .collect(Collectors.toList());

        // 7. 返回分页结果
        Page<FriendRequestVo> voPage = new Page<>(reqPage.getCurrent(), reqPage.getSize(), reqPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }


    @Override
    @Transactional
    public void passFriendRequest(String friendRequestId, String friendRemark) {
        if (StringUtils.isAnyBlank(friendRequestId)) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }

        // 1.查询得到好友请求. 
        FriendRequest friendRequest = friendRequestMapper.selectById(friendRequestId);
        if (friendRequest == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }

        // 2.创建双方关系
        // note 需要设置俩张表的时间, 所以提前抽取出来, 这样时间更加同意.
        LocalDateTime now = LocalDateTime.now();
        Long myId = friendRequest.getMyId();
        Long friendId = friendRequest.getFriendId();
        // 本人的
        Friendship friendship = new Friendship();
        friendship.setMyId(friendId);
        friendship.setFriendId(myId);
        friendship.setFriendRemark(friendRemark);
        friendship.setIsMsgIgnore(YesOrNo.NO.type);
        friendship.setIsBlack(YesOrNo.NO.type);
        friendship.setCreateTime(now);
        // 对方的, 即请求者
        Friendship reqeustFriendShip = new Friendship();
        reqeustFriendShip.setMyId(myId);
        reqeustFriendShip.setFriendId(friendId);
        reqeustFriendShip.setFriendRemark(friendRequest.getFriendRemark());
        reqeustFriendShip.setIsMsgIgnore(YesOrNo.NO.type);
        reqeustFriendShip.setIsBlack(YesOrNo.NO.type);
        reqeustFriendShip.setCreateTime(now);
        // 插入数据库
        friendshipMapper.insert(reqeustFriendShip);
        friendshipMapper.insert(friendship);

        // 3.需要设置通过
        friendRequest.setVerifyStatus(FriendRequestVerifyStatus.SUCCESS.type);
        friendRequestMapper.updateById(friendRequest);
        // 同时还需要注意将有可能双方都用通过的可能, 需要都设置通过
        QueryWrapper<FriendRequest> friendRequestQueryWrapper = new QueryWrapper<>();
        friendRequestQueryWrapper.eq("friend_id", myId).eq("my_id", friendId);
        FriendRequest requestOpposite = new FriendRequest();
        requestOpposite.setVerifyStatus(FriendRequestVerifyStatus.SUCCESS.type);
        friendRequestMapper.update(requestOpposite, friendRequestQueryWrapper);
    }

    @Override
    public void refuseFriendRequest(String friendRequestId) {
        if (StringUtils.isAnyBlank(friendRequestId)) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        // 1.查询得到好友请求. 
        FriendRequest friendRequest = friendRequestMapper.selectById(friendRequestId);
        if (friendRequest == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        // 3.需要设置通过
        friendRequest.setVerifyStatus(FriendRequestVerifyStatus.FAIL.type);
        friendRequestMapper.updateById(friendRequest);
        QueryWrapper<FriendRequest> friendRequestQueryWrapper = new QueryWrapper<>();
        friendRequestQueryWrapper.eq("friend_id", friendRequest.getMyId()).eq("my_id", friendRequest.getMyId());
        FriendRequest requestOpposite = new FriendRequest();
        requestOpposite.setVerifyStatus(FriendRequestVerifyStatus.FAIL.type);
        friendRequestMapper.update(requestOpposite, friendRequestQueryWrapper);
    }

    @Override
    public FriendRequestVo getFriendRequestVo(FriendRequest friendRequest) {
        if (friendRequest == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        FriendRequestVo friendRequestVo = new FriendRequestVo();    
        BeanUtils.copyProperties(friendRequest, friendRequestVo);
        Long friendId = friendRequestVo.getFriendId();
        Users friendUser = usersService.getById(friendId);
        friendRequestVo.setFriendUser(usersService.getUserVo(friendUser));
        return friendRequestVo;
    }

}
