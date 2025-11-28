-- auto-generated definition
CREATE DATABASE cloudchat
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;



use cloudchat;

DROP TABLE IF EXISTS `users`;
create table users
(
    id               bigint auto_increment comment 'id'
        primary key,
    wechat_num       varchar(64)                            null comment '微信号',
    account          varchar(64)                            not null comment '登录账号（唯一）',
    password         varchar(128)                           not null comment '密码（加密存储）',
    wechat_num_img   varchar(256)                           null comment '微信号二维码',
    mobile           varchar(11)                            null comment '手机号',
    nickname         varchar(16)                            null comment '昵称',
    real_name        varchar(16) charset utf8mb4            null comment '真实姓名',
    sex              int                                    null comment '性别，1:男 0:女 2:保密',
    face             varchar(128) charset utf8mb4           null comment '用户头像',
    email            varchar(128)                           null comment '邮箱',
    birthday         date                                   null comment '生日',
    country          varchar(32)                            null comment '国家',
    province         varchar(32)                            null comment '省份',
    city             varchar(32)                            null comment '城市',
    district         varchar(32)                            null comment '区县',
    chat_bg          varchar(256)                           null comment '聊天背景',
    friend_circle_bg varchar(256)                           null comment '朋友圈背景图',
    signature        varchar(128)                           null comment '我的一句话签名',
    user_role        varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    edit_time        datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    create_time      datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete        tinyint      default 0                 not null comment '是否删除'
)
    comment '用户表';



DROP TABLE IF EXISTS `friendship`;
CREATE TABLE `friendship`
(
    `id`            varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `my_id`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自己的用户id',
    `friend_id`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '我朋友的id',
    `friend_remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '好友的备注名',
    `is_msg_ignore` int                                                          NOT NULL COMMENT '是否消息免打扰，0-打扰，不忽略消息(默认)；1-免打扰，忽略消息',
    `is_black`      int                                                          NOT NULL COMMENT '是否拉黑，0-好友(默认)；1-已拉黑',
    `create_time`   datetime default CURRENT_TIMESTAMP                           not null comment '创建时间',
    `update_time`   datetime default CURRENT_TIMESTAMP                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `my_id` (`my_id`, `friend_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='朋友关系表';


DROP TABLE IF EXISTS `friend_request`;
CREATE TABLE `friend_request`
(
    `id`             varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci                            NOT NULL,
    `my_id`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci                            NOT NULL COMMENT '添加好友，发起请求的用户id',
    `friend_id`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci                            NOT NULL COMMENT '要添加的朋友的id',
    `friend_remark`  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '好友的备注名',
    `verify_message` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求的留言，验证消息',
    `verify_status`  int                                                                                     NOT NULL COMMENT '请求被好友审核的状态，0-待审核；1-已添加，2-已过期',
    `request_time`   datetime                                                      default CURRENT_TIMESTAMP not null comment '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='好友请求记录表';


DROP TABLE IF EXISTS `friend_circle`;
CREATE TABLE `friend_circle`
(
    `id`            bigint   NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `user_id`       bigint   NOT NULL COMMENT '发朋友圈的用户id',
    `words`         varchar(256)      DEFAULT NULL COMMENT '文字内容',
    `images`        varchar(2560)     DEFAULT NULL COMMENT '图片内容，url用逗号分割',
    `video`         varchar(256)      DEFAULT NULL COMMENT '视频url',
    `visible_scope` tinyint  NOT NULL DEFAULT 1 COMMENT '可见范围：0=仅自己可见 1=好友可见 2=所有人可见',
    `create_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '朋友圈表';


DROP TABLE IF EXISTS `friend_circle_liked`;
CREATE TABLE `friend_circle_liked`
(
    `id`               bigint   NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `belong_user_id`   bigint   NOT NULL COMMENT '朋友圈归属用户的id',
    `friend_circle_id` bigint   NOT NULL COMMENT '点赞的那个朋友圈id',
    `liked_user_id`    bigint   NOT NULL COMMENT '点赞的那个用户id',
    `create_time`      datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='点赞朋友圈的朋友';


DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `belong_user_id`   bigint       NOT NULL COMMENT '朋友圈所属用户id（这条朋友圈的主人）',
    `father_id`        bigint                DEFAULT NULL COMMENT '父评论id（如果是回复，则此字段为父评论）',
    `friend_circle_id` bigint       NOT NULL COMMENT '关联的朋友圈id',
    `comment_user_id`  bigint       NOT NULL COMMENT '评论人用户id',
    `reply_to_user_id` bigint                DEFAULT NULL COMMENT '被回复的用户id（一级评论可为朋友圈主人或空）',
    `comment_content`  varchar(512) NOT NULL COMMENT '评论内容',
    `created_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    `updated_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`        tinyint(1)   NOT NULL DEFAULT 0 COMMENT '是否删除：0-正常，1-已删除',
    `status`           tinyint(1)   NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-屏蔽/违规',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_circle_time` (`friend_circle_id`, `created_time`) USING BTREE,
    KEY `idx_father_id` (`father_id`) USING BTREE,
    KEY `idx_comment_user` (`comment_user_id`) USING BTREE,
    KEY `idx_belong_user` (`belong_user_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT ='朋友圈评论表';

DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`
(
    `id`              bigint      NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `msg_id`          varchar(64) not null comment '前端生成的消息ID（用于去重和状态同步）',
    `sender_id`       bigint      NOT NULL COMMENT '发送者用户id',
    `receiver_id`     bigint      NOT NULL COMMENT '接收者id（用户或群）',
    `chat_type`       tinyint     NOT NULL                     DEFAULT 1 COMMENT '接收者类型：1=用户，2=群组',
    `content`         varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文本消息内容',
    `msg_type`        tinyint     NOT NULL COMMENT '消息类型：1=文本，2=图片，3=视频，4=语音等',
    `chat_time`       datetime    NOT NULL COMMENT '消息时间（发送/接收时间）',

    `video_cover_url` varchar(256) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '视频封面地址',
    `media_url`       varchar(256) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '视频地址',
    `media_width`     int                                      DEFAULT NULL COMMENT '视频宽度',
    `media_height`    int                                      DEFAULT NULL COMMENT '视频高度',
    `video_times`     int                                      DEFAULT NULL COMMENT '视频时长（秒）',

    `voice_url`       varchar(256) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '语音地址',
    `voice_duration`  int                                      DEFAULT NULL COMMENT '语音时长（秒）',

    `is_read`         tinyint(1)  NOT NULL                     DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
    `is_delete`       tinyint(1)  NOT NULL                     DEFAULT 0 COMMENT '是否删除：0=否，1=是',

    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_sender_receiver_time` (`sender_id`, `receiver_id`, `chat_time`),
    KEY `idx_receiver_time` (`receiver_id`, `chat_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT ='聊天信息存储表';

