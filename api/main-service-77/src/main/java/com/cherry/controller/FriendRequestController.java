package com.cherry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cherry.dto.user.AddUsersRequest;
import com.cherry.dto.user.UserUpdateRequest;
import com.cherry.dto.user.UsersQueryRequest;
import com.cherry.exceptions.GraceException;
import com.cherry.grace.result.GraceJSONResult;
import com.cherry.grace.result.ResponseStatusEnum;
import com.cherry.pojo.FriendRequest;
import com.cherry.pojo.Users;
import com.cherry.service.FriendRequestService;
import com.cherry.service.UsersService;
import com.cherry.vo.FriendRequestVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author cherry
 */
@RestController
@RequestMapping("/friendRequest")
public class FriendRequestController {
    
    @Resource
    private FriendRequestService friendRequestService;

    /**
     * 根据id得到好友请求数据
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public GraceJSONResult getFriendRequestVo(long id) {
        if (id <= 0) {
            GraceException.display(ResponseStatusEnum.PARAMS_NULL);
        }
        FriendRequest friendRequest = friendRequestService.getById(id);
        return GraceJSONResult.ok(friendRequestService.getFriendRequestVo(friendRequest));
    }

    /**
     * 添加好友请求
     * @param addUsersRequest
     * @return
     */
    @PostMapping("/add")
    public GraceJSONResult add(@RequestBody AddUsersRequest addUsersRequest) {  
        friendRequestService.addFriendRequest(addUsersRequest);
        return GraceJSONResult.ok();
    }

    /**
     * 查看好友请求列表.
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public GraceJSONResult list(@RequestParam(defaultValue = "1", name = "currentPage") Long currentPage,
                                @RequestParam(defaultValue = "5", name = "pageSize") Long pageSize,
                                HttpServletRequest request) {
        Page<FriendRequestVo> pageResult = friendRequestService
                .getFriendRequestVoPage(currentPage, pageSize, request);

        return GraceJSONResult.ok(pageResult);
    }

    /**
     * 同意好友请求
     * @param friendRequestId
     * @param friendRemark
     * @return
     */
    @PostMapping("/pass")
    public GraceJSONResult pass(String friendRequestId, String friendRemark) {
        friendRequestService.passFriendRequest(friendRequestId, friendRemark);
        return GraceJSONResult.ok();
    }

    /**
     * 拒绝好友请求
     * @param friendRequestId
     * @return
     */
    @PostMapping("/refuse")
    public GraceJSONResult refuse(String friendRequestId) {
        friendRequestService.refuseFriendRequest(friendRequestId);
        return GraceJSONResult.ok();
    }
    
    
}
