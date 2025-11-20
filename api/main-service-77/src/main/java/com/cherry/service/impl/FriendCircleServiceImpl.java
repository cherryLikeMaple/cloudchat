package com.cherry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.dto.friendCycle.CreateFriendCircleDTO;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.mapper.FriendCircleMapper;
import com.cherry.pojo.FriendCircle;
import com.cherry.pojo.Friendship;
import com.cherry.pojo.Users;
import com.cherry.service.*;
import com.cherry.vo.TinyUserVO;
import com.cherry.vo.FriendCircleVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 朋友圈表 服务实现类
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
@Service
public class FriendCircleServiceImpl extends ServiceImpl<FriendCircleMapper, FriendCircle> implements FriendCircleService {

    @Resource
    private UsersService usersService;
    @Resource
    private FriendshipService friendshipService;
    @Resource
    private FriendCircleLikedService friendCircleLikedService;
    @Resource
    @Lazy
    private CommentService commentService;


    @Override
    public void publish(CreateFriendCircleDTO createFriendCircleDTO, Long userId) {

        // 1. DTO -> Entity
        FriendCircle friendCircle = new FriendCircle();
        friendCircle.setUserId(userId);
        friendCircle.setWords(createFriendCircleDTO.getWords());
        // 图片list -> 逗号分割
        if (createFriendCircleDTO.getImages() != null &&
                !createFriendCircleDTO.getImages().isEmpty()) {
            // note 如果是标签类型的数据这存入数据库, 要在前后都添加都好 ,Java, 这样like查询不会查到 javascript
            // note 但是这里存放图片所以同时查到同一个的概率很低. 
            String images = String.join(",", createFriendCircleDTO.getImages());
            friendCircle.setImages(images);
        }
        friendCircle.setVideo(createFriendCircleDTO.getVideo());

        // 2.保存
        boolean result = this.save(friendCircle);
        if (!result) {
            GraceException.display(ResponseStatusEnum.SYSTEM_IO);
        }
    }

    @Override
    public Page<FriendCircleVO> listFriendCircleByUser(Long loginUserId, Long friendId, Integer currentPage, Integer pageSize) {
        Page<FriendCircle> pageInfo = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<FriendCircle> friendCircleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendCircleLambdaQueryWrapper.eq(FriendCircle::getUserId, friendId)
                .orderByDesc(FriendCircle::getCreateTime);

        this.page(pageInfo, friendCircleLambdaQueryWrapper);

        List<FriendCircleVO> friendCircleVos = buidlVoList(pageInfo.getRecords(), loginUserId);

        Page<FriendCircleVO> voPage = new Page<>(currentPage, pageSize);
        voPage.setRecords(friendCircleVos);
        voPage.setTotal(pageInfo.getTotal());
        voPage.setPages(pageInfo.getPages());

        return voPage;
    }

    @Override
    public Page<FriendCircleVO> listAllFriendCircle(Long loginUserId, Integer currentPage, Integer pageSize) {
        // 1. 查出我所有的好友 id 列表
        List<Friendship> friendshipList = friendshipService.list(
                new LambdaQueryWrapper<Friendship>()
                        .eq(Friendship::getMyId, loginUserId)
                        .eq(Friendship::getIsBlack, 0)
        );
        List<Long> userIdList = friendshipList.stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toList());
        if (userIdList.isEmpty() && loginUserId == null) {
            return new Page<FriendCircleVO>(currentPage, pageSize).setRecords(Collections.emptyList());
        }
        userIdList.add(loginUserId);

        // 2. 查朋友圈
        Page<FriendCircle> friendCirclePage = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<FriendCircle> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(FriendCircle::getUserId, userIdList)
                .orderByDesc(FriendCircle::getCreateTime);

        this.page(friendCirclePage, wrapper);

        // 3. 转 VO
        List<FriendCircleVO> friendCircleVos = buidlVoList(friendCirclePage.getRecords(), loginUserId);

        // 4. 构造 VO 的分页对象
        Page<FriendCircleVO> voPage = new Page<>(currentPage, pageSize);
        voPage.setRecords(friendCircleVos);
        voPage.setTotal(friendCirclePage.getTotal());
        voPage.setPages(friendCirclePage.getPages());

        return voPage;
    }

    @Override
    public void batchSetCircleLiked(Long circleId, Long loginUserId, FriendCircleVO friendCircleVO) {
        // 设置当前用户是否点赞. 
        friendCircleVO.setLiked(friendCircleLikedService.isLiked(circleId, loginUserId));
        // 设置给这条朋友圈点赞的所有用户, 如果有的话.
        List<TinyUserVO> likedUsers = friendCircleLikedService.getLikedUsers(circleId);
        if (likedUsers != null && !likedUsers.isEmpty()) {
            friendCircleVO.setLikedUserList(likedUsers);
            friendCircleVO.setLikeCount(likedUsers.size());
        }
    }

    @Override
    public FriendCircleVO getFriendCircle(Long circleId, Long loginUserId) {
        FriendCircle friendCircle = this.getById(circleId);
        FriendCircleVO friendCircleVO = new FriendCircleVO();
        BeanUtils.copyProperties(friendCircle, friendCircleVO);
        Users user = usersService.getById(friendCircle.getUserId());
        friendCircleVO.setNickname(user.getNickname());
        friendCircleVO.setFace(user.getFace());
        // 设置点赞相关的. 
        this.batchSetCircleLiked(friendCircle.getId(), loginUserId, friendCircleVO);
        return friendCircleVO;
    }


    private List<FriendCircleVO> buidlVoList(List<FriendCircle> friendCircleList, long loginUserId) {
        if (friendCircleList == null || friendCircleList.isEmpty()) {
            return Collections.emptyList();
        }
        return friendCircleList.stream().map(circle -> {
            FriendCircleVO friendCircleVO = new FriendCircleVO();
            Long circleId = circle.getId();
            friendCircleVO.setId(circleId);
            Long userId = circle.getUserId();
            friendCircleVO.setUserId(userId);
            Users friendCycleUser = usersService.getById(userId);
            friendCircleVO.setNickname(friendCycleUser.getNickname());
            friendCircleVO.setFace(friendCycleUser.getFace());
            friendCircleVO.setWords(circle.getWords());
            friendCircleVO.setVideo(circle.getVideo());
            friendCircleVO.setCreateTime(circle.getCreateTime());
            // 图片特殊处理
            if (StringUtils.isNotBlank(circle.getImages())) {
                friendCircleVO.setImageList(Arrays.asList(circle.getImages().split(",")));
            }
            // 设置点赞相关的参数
            this.batchSetCircleLiked(circleId, loginUserId, friendCircleVO);
            friendCircleVO.setCommentCount(commentService.listCommentsByCircleId(circleId).getTotal());
            return friendCircleVO;
        }).collect(Collectors.toList());
    }
}
