package com.cherry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.dto.friendcCircleComment.CommentAddRequest;
import com.cherry.mapper.CommentMapper;
import com.cherry.pojo.Comment;
import com.cherry.pojo.FriendCircle;
import com.cherry.pojo.Users;
import com.cherry.service.CommentService;
import com.cherry.service.FriendCircleService;
import com.cherry.service.UsersService;
import com.cherry.vo.CircleUserVO;
import com.cherry.vo.CommentListResult;
import com.cherry.vo.CommentVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author cherry
 * @since 2025-11-17
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    @Resource
    private UsersService usersService;
    @Resource
    private FriendCircleService friendCircleService;

    @Override
    public Long addComment(CommentAddRequest request, Long currentUserId) {
        // 1. 基础校验
        if (request == null || request.getFriendCircleId() == null) {
            throw new IllegalArgumentException("朋友圈id不能为空");
        }
        if (StringUtils.isBlank(request.getCommentContent())) {
            throw new IllegalArgumentException("评论内容不能为空");
        }

        Long friendCircleId = request.getFriendCircleId();
        Long parentCommentId = request.getParentCommentId();
        Long replyToUserId = request.getReplyToUserId();
        
        // 查询朋友圈, 得到父评论
        FriendCircle friendCircle  = friendCircleService.getById(friendCircleId);
        if (friendCircle == null) {
            throw new IllegalArgumentException("朋友圈不存在");
        }
        // 得到发布朋友圈的人
        Long belongUserId = friendCircle.getUserId();
        Long fatherId = null;
        if (parentCommentId == null || parentCommentId <= 0) {
            fatherId = null;
            if (replyToUserId == null || replyToUserId <= 0) {
                replyToUserId = belongUserId;
            }
        } else {
            fatherId = parentCommentId;
            Comment parentComment = this.getById(parentCommentId);
            if (parentComment == null || !friendCircleId.equals(parentComment.getFriendCircleId())) {
                throw new IllegalArgumentException("父评论不存在或不属于当前朋友圈");
            }
            // 如果没有明确指定 replyToUserId，则默认回复的是父评论的发起人
            if (replyToUserId == null || replyToUserId <= 0) {
                replyToUserId = parentComment.getCommentUserId();
            }
        }
        Comment comment = new Comment();
        comment.setBelongUserId(belongUserId);
        comment.setFatherId(fatherId);
        comment.setFriendCircleId(friendCircleId);
        comment.setCommentUserId(currentUserId);
        comment.setReplyToUserId(replyToUserId);
        comment.setCommentContent(request.getCommentContent().trim());
        
        this.save(comment);
        
        return comment.getId();
    }
    
    public CommentVO transferComment(Comment comment) {
        CommentVO commentVO = new CommentVO();
        commentVO.setCommentId(comment.getId());
        commentVO.setFriendCircleId(comment.getFriendCircleId());
        commentVO.setParentCommentId(comment.getFatherId());
        commentVO.setCommentContent(comment.getCommentContent());
        commentVO.setCreateTime(comment.getCreatedTime());
        // 评论者
        Users commentingUsers = usersService.getById(comment.getCommentUserId());
        commentVO.setCommentUser(CircleUserVO.fromEntity(commentingUsers));
        // 被评论的对象.
        if (comment.getReplyToUserId() != null) {
            Users commentedUsers = usersService.getById(comment.getReplyToUserId());
            commentVO.setReplyToUser(CircleUserVO.fromEntity(commentedUsers));
        }
        return commentVO;
    }
    public CommentVO transferComment(Comment comment, Map<Long, Users> userMap) {
        CommentVO commentVO = new CommentVO();
        commentVO.setCommentId(comment.getId());
        commentVO.setFriendCircleId(comment.getFriendCircleId());
        commentVO.setParentCommentId(comment.getFatherId());
        commentVO.setCommentContent(comment.getCommentContent());
        commentVO.setCreateTime(comment.getCreatedTime());
        // 评论者
        Users commentingUsers = userMap.get(comment.getCommentUserId());
        commentVO.setCommentUser(CircleUserVO.fromEntity(commentingUsers));
        // 被评论的对象.
        if (comment.getReplyToUserId() != null) {
            Users commentedUsers = userMap.get(comment.getReplyToUserId());
            commentVO.setReplyToUser(CircleUserVO.fromEntity(commentedUsers));
        }
        return commentVO;
    }

    @Override
    public CommentListResult listCommentsByCircleId(Long circleId) {
        
        List<Comment> commentList = this.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getFriendCircleId, circleId)
                .eq(Comment::getStatus, 0)
                .eq(Comment::getIsDelete, 0)
                .orderByAsc(Comment::getCreatedTime));

        if (CollectionUtils.isEmpty(commentList)) {
            return new CommentListResult();
        }

        // 先把所有评论转成 CommentVO，放到 map 里方便后面组树
        HashMap<Long, CommentVO> voMap = new HashMap<>(commentList.size());
        Set<Long> userIds = new HashSet<>();
        for (Comment comment : commentList) {
            userIds.add(comment.getCommentUserId());
            if (comment.getReplyToUserId() != null) {
                userIds.add(comment.getReplyToUserId());
            }
        }
        List<Users> userList = usersService.listByIds(userIds);
        Map<Long, Users> usersMap = userList.stream()
                .collect(Collectors.toMap(Users::getId, u -> u));
        for (Comment comment : commentList) {
            CommentVO commentVO = transferComment(comment, usersMap);
            commentVO.setChildren(new ArrayList<>());
            voMap.put(comment.getId(), commentVO);
        }
        // 组装树接结构: 父id = null / 0 作为根, 其余挂都挂在这下面.
        ArrayList<CommentVO> rootList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentVO currentVO = voMap.get(comment.getId());
            Long parentId = comment.getFatherId();
            if (parentId == null || parentId == 0) {
                rootList.add(currentVO);
            } else {
                CommentVO parentVO = voMap.get(parentId);
                if (parentVO != null) {
                    parentVO.getChildren().add(currentVO);
                } else {
                    rootList.add(currentVO);
                }
            }
        }

        CommentListResult commentListResult = new CommentListResult();
        commentListResult.setTotal(commentList.size());
        commentListResult.setList(rootList);
        return commentListResult;
    }

    @Override
    public void deleteCommentById(Long commentId, Long loginUserId) {
        Comment comment  = this.getById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }

        Long commentUserId = comment.getCommentUserId(); 
        Long friendCircleId = comment.getFriendCircleId();

        FriendCircle friendCircle = friendCircleService.getById(friendCircleId);
        if (friendCircle == null) {
            throw new IllegalArgumentException("朋友圈不存在");
        }
        Long belongUserId  = friendCircle.getUserId();

        if (!loginUserId.equals(commentUserId) && !loginUserId.equals(belongUserId)) {
            throw new RuntimeException("无权删除该评论");
        }

        this.removeById(commentId);
    }
}
