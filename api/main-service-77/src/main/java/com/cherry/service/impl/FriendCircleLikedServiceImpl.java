package com.cherry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.controller.FriendCircleLikedController;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.mapper.FriendCircleLikedMapper;
import com.cherry.mapper.FriendCircleMapper;
import com.cherry.pojo.FriendCircle;
import com.cherry.pojo.FriendCircleLiked;
import com.cherry.pojo.Users;
import com.cherry.service.FriendCircleLikedService;
import com.cherry.service.FriendCircleService;
import com.cherry.service.UsersService;
import com.cherry.vo.CircleUserVO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 点赞朋友圈的朋友 服务实现类
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
@Service
public class FriendCircleLikedServiceImpl extends ServiceImpl<FriendCircleLikedMapper, FriendCircleLiked> implements FriendCircleLikedService {

    @Resource
    private FriendCircleMapper friendCircleMapper;
    @Resource
    private UsersService usersService;

    @Override
    public boolean likeOrUnlike(Long userId, Long circleId) {
        LambdaQueryWrapper<FriendCircleLiked> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FriendCircleLiked::getId, circleId)
                .eq(FriendCircleLiked::getLikedUserId, userId);
        FriendCircleLiked like = this.getOne(wrapper);
        if (like == null) {
            // 没点过 -> 点赞
            FriendCircleLiked friendCircleLiked = new FriendCircleLiked();
            friendCircleLiked.setBelongUserId(friendCircleMapper.selectById(circleId).getUserId());
            friendCircleLiked.setFriendCircleId(circleId);
            friendCircleLiked.setLikedUserId(userId);
            boolean result = this.save(friendCircleLiked);
            if (!result) {
                GraceException.display(ResponseStatusEnum.SYSTEM_ERROR);
            }
            return true;
        } else {
            boolean remove = this.remove(wrapper);
            if (!remove) {
                GraceException.display(ResponseStatusEnum.SYSTEM_ERROR);
            }
            return false;
        }
    }

    @Override
    public long countLike(Long circleId) {
        return this.count(
                new LambdaQueryWrapper<FriendCircleLiked>()
                        .eq(FriendCircleLiked::getFriendCircleId, circleId)
        );
    }

    @Override
    public boolean isLiked(Long circleId, Long loginUserId) {
        return this.count(
                new LambdaQueryWrapper<FriendCircleLiked>()
                        .eq(FriendCircleLiked::getLikedUserId, loginUserId)
                        .eq(FriendCircleLiked::getFriendCircleId, circleId)
        ) > 0;
    }

    @Override
    public List<CircleUserVO> getLikedUsers(Long circleId) {
        List<FriendCircleLiked> likedList = this.list(
                new LambdaQueryWrapper<FriendCircleLiked>()
                        .eq(FriendCircleLiked::getFriendCircleId, circleId)
        );

        if (likedList != null && !likedList.isEmpty()) {
            Set<Long> likeUserIds = likedList.stream()
                    .map(FriendCircleLiked::getLikedUserId)
                    .collect(Collectors.toSet());
            List<Users> likeUsers = usersService.listByIds(likeUserIds);
            return likeUsers.stream().map(CircleUserVO::fromEntity).collect(Collectors.toList());
        }
        return null;
    }
}
