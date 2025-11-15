package com.cherry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.mapper.FriendshipMapper;
import com.cherry.pojo.Friendship;
import com.cherry.service.FriendshipService;
import org.springframework.stereotype.Service;

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

}
