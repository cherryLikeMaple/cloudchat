package com.cherry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.api.feign.UserInfoMicroServiceFeign;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.mapper.FriendshipMapper;
import com.cherry.pojo.Friendship;
import com.cherry.pojo.Users;
import com.cherry.service.FriendshipService;
import com.cherry.service.UsersService;
import com.cherry.vo.FriendshipVo;
import com.cherry.vo.UserVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 朋友关系表 服务实现类
 * </p>
 *
 * @author cherry
 * @since 2025-11-15
 */
@Service
public class FriendshipServiceImpl extends ServiceImpl<FriendshipMapper, Friendship> implements FriendshipService {

    @Resource
    private FriendshipMapper friendshipMapper;
    @Resource
    private UsersService usersService;
    @Resource
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;

    @Override
    public FriendshipVo getFriendShipVo(Long friendId, Long myId) {
        QueryWrapper<Friendship> friendshipQueryWrapper = new QueryWrapper<>();
        friendshipQueryWrapper.eq("friend_id", friendId);
        friendshipQueryWrapper.eq("my_id", myId);
        Friendship friendship = friendshipMapper.selectOne(friendshipQueryWrapper);
        if (friendship == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        return this.getFriendShipVo(friendship);
    }

    @Override
    public FriendshipVo getFriendShipVo(Friendship friendShip) {
        FriendshipVo friendshipVo = new FriendshipVo();
        if (friendShip == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        BeanUtils.copyProperties(friendShip, friendshipVo);
        UserVo friendVo = usersService.getUserVo(usersService.getById(friendShip.getFriendId()));
        if (friendVo == null) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        friendshipVo.setFriendUser(friendVo);
        return friendshipVo;
    }

    @Override
    public Page<FriendshipVo> getFriendShipVoPage(Long currentPage,
                                                  Long pageSize,
                                                  HttpServletRequest request,
                                                  Integer isBlack) {
        // 1.获取当前用户
        Users loginUser = userInfoMicroServiceFeign.getLoginUser(request.getHeader("Authorization"));
        Long myId = loginUser.getId();

        // 2.构造分页参数
        Page<Friendship> page = new Page<>(currentPage, pageSize);

        // 3.查找我的好友（不带黑名单条件）
        LambdaQueryWrapper<Friendship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friendship::getMyId, myId);
                // 只有 isBlack 不为 null 时才拼 is_black 条件
//                .eq(isBlack != null, Friendship::getIsBlack, isBlack);

        Page<Friendship> myFriendsPage = this.page(page, wrapper);
        List<Friendship> myFriends = myFriendsPage.getRecords();
        if (myFriends.isEmpty()) {
            Page<FriendshipVo> empty = new Page<>(myFriendsPage.getCurrent(),
                    myFriendsPage.getSize(),
                    myFriendsPage.getTotal());
            empty.setRecords(Collections.emptyList());
            return empty;
        }

        // 4.收集好友的个人信息
        Set<Long> friendIds = myFriends.stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        // 5.批量查询好友信息
        List<Users> friendUsers = usersService.listByIds(friendIds);
        Map<Long, UserVo> friendUsersVoMap = friendUsers.stream()
                .collect(Collectors.toMap(
                        Users::getId,
                        usersService::getUserVo
                ));

        // 6.组装FriendShipVo
        List<FriendshipVo> voList = myFriends.stream()
                .map(friendship -> {
                    FriendshipVo friendshipVo = new FriendshipVo();
                    BeanUtils.copyProperties(friendship, friendshipVo);

                    UserVo userVo = friendUsersVoMap.get(friendship.getFriendId());
                    friendshipVo.setFriendUser(userVo);

                    return friendshipVo;
                })
                .toList();

        // 7.返回分页结果
        Page<FriendshipVo> voPage = new Page<>(myFriendsPage.getCurrent(),
                myFriendsPage.getSize(),
                myFriendsPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }


    @Override
    public void updateFriendRemark(Long myId, Long friendId, String remark) {
        LambdaUpdateWrapper<Friendship> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Friendship::getMyId, myId)
                .eq(Friendship::getFriendId, friendId)
                .set(Friendship::getFriendRemark, remark);

        this.update(wrapper);
    }

    @Override
    public void updateBlackList(Long myId, Long friendId, int yesOrNo) {
        LambdaUpdateWrapper<Friendship> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Friendship::getMyId, myId)
                .eq(Friendship::getFriendId, friendId)
                .set(Friendship::getIsBlack, yesOrNo);

        this.update(wrapper);
    }

    @Override
    public void deleteFriend(Long myId, Long friendId) {
        LambdaUpdateWrapper<Friendship> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Friendship::getMyId, myId)
                .eq(Friendship::getFriendId, friendId);

        this.remove(wrapper);
    }

    @Override
    public boolean isBlack(Long myId, Long friendId) {
        LambdaUpdateWrapper<Friendship> wrapper1 = new LambdaUpdateWrapper<>();
        wrapper1.eq(Friendship::getMyId, myId)
                .eq(Friendship::getFriendId, friendId)
                .eq(Friendship::getIsBlack, 1);

        Friendship friendship1 = this.getOne(wrapper1);

        LambdaUpdateWrapper<Friendship> wrapper2 = new LambdaUpdateWrapper<>();
        wrapper2.eq(Friendship::getMyId, friendId)
                .eq(Friendship::getFriendId, myId)
                .eq(Friendship::getIsBlack, 1);

        Friendship friendship2 = this.getOne(wrapper2);
        
        return friendship1 != null || friendship2 != null;
    }


}
