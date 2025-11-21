-- auto-generated definition
use cloudchat;

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

INSERT INTO users (id, wechat_num, account, password, wechat_num_img, mobile, nickname, real_name, sex, face, email,
                   birthday, country, province, city, district, chat_bg, friend_circle_bg, signature, user_role,
                   edit_time, create_time, update_time, is_delete)
VALUES (4, 'wechat_iceblue', 'iceblue', '4fde5058ac874f8e3f97cb443bcaba28', NULL, '13311110004', 'IceBlue', '韩冰', 2,
        'http://127.0.0.1:9000/cloudcloudchat/face/4/avatar4.jpg', 'iceblue@gmail.com', '2000-12-03', '中国', '北京',
        '朝阳', '望京', 'http://127.0.0.1:9000/cloudchat/chatbg/bg4.jpg',
        'http://127.0.0.1:9000/cloudchat/friendbg/fb4.jpg', '保持冷静，保持热爱', 'user', '2025-11-09 10:22:45',
        '2025-11-09 10:22:45', '2025-11-11 17:01:05', 0),

       (5, 'wechat_nova', 'nova', '4fde5058ac874f8e3f97cb443bcaba28', NULL, '13311110005', 'Nova', '江辰', 1,
        'http://127.0.0.1:9000/cloudchat/face/5/avatar5.jpg', 'nova@outlook.com', '2004-04-09', '中国', '江苏', '苏州',
        '姑苏', 'http://127.0.0.1:9000/cloudchat/chatbg/bg5.jpg', 'http://127.0.0.1:9000/cloudchat/friendbg/fb5.jpg',
        '星光不问赶路人', 'user', '2025-11-08 16:33:29', '2025-11-08 16:33:29', '2025-11-14 10:55:01', 0),

       (6, 'wechat_sakura', 'sakura', '4fde5058ac874f8e3f97cb443bcaba28', NULL, '13311110006', 'Sakura', '小樱', 0,
        'http://127.0.0.1:9000/cloudchat/face/6/avatar6.jpg', 'sakura@qq.com', '2003-02-17', '日本', '东京', '港区',
        '六本木', 'http://127.0.0.1:9000/cloudchat/chatbg/bg6.jpg', 'http://127.0.0.1:9000/cloudchat/friendbg/fb6.jpg',
        '世界灿烂，欢迎加入', 'user', '2025-11-08 12:03:22', '2025-11-08 12:03:22', '2025-11-11 09:11:11', 0),

       (7, 'wechat_shadow', 'shadow', '4fde5058ac874f8e3f97cb443bcaba28', NULL, '13311110007', 'Shadow', '江影', 1,
        'http://127.0.0.1:9000/cloudchat/face/7/avatar7.jpg', 'shadow@gmail.com', '2001-03-06', '中国', '四川', '成都',
        '武侯', 'http://127.0.0.1:9000/cloudchat/chatbg/bg7.jpg', 'http://127.0.0.1:9000/cloudchat/friendbg/fb7.jpg',
        '影子也会发光', 'user', '2025-11-07 11:51:01', '2025-11-07 11:51:01', '2025-11-10 14:20:12', 0),

       (8, 'wechat_luna', 'luna', '4fde5058ac874f8e3f97cb443bcaba28', NULL, '13311110008', 'Luna', '李月', 0,
        'http://127.0.0.1:9000/cloudchat/face/8/avatar8.jpg', 'luna@foxmail.com', '2002-07-29', '中国', '福建', '厦门',
        '思明', 'http://127.0.0.1:9000/cloudchat/chatbg/bg8.jpg', 'http://127.0.0.1:9000/cloudchat/friendbg/fb8.jpg',
        '月亮会一直等你', 'user', '2025-11-07 09:44:31', '2025-11-07 09:44:31', '2025-11-12 19:05:44', 0),

       (9, 'wechat_odin', 'odin', '4fde5058ac874f8e3f97cb443bcaba28', NULL, '13311110009', 'Odin', '欧丁', 1,
        'http://127.0.0.1:9000/cloudchat/face/9/avatar9.jpg', 'odin@163.com', '1999-09-09', '德国', '柏林', '米特',
        '中心区', 'http://127.0.0.1:9000/cloudchat/chatbg/bg9.jpg', 'http://127.0.0.1:9000/cloudchat/friendbg/fb9.jpg',
        '极致的光，来自深处的暗', 'user', '2025-11-06 15:31:21', '2025-11-06 15:31:21', '2025-11-12 11:09:30', 0),

       (10, 'wechat_peach', 'peach', '4fde5058ac874f8e3f97cb443bcaba28', NULL, '13311110010', 'Peach', '桃子', 0,
        'http://127.0.0.1:9000/cloudchat/face/10/avatar10.jpg', 'peach@gmail.com', '2004-01-18', '中国', '浙江', '宁波',
        '鄞州', 'http://127.0.0.1:9000/cloudchat/chatbg/bg10.jpg', 'http://127.0.0.1:9000/cloudchat/friendbg/fb10.jpg',
        '可爱永不过期', 'user', '2025-11-06 13:13:45', '2025-11-06 13:13:45', '2025-11-13 16:26:14', 0);



DROP TABLE IF EXISTS `friendship`;
CREATE TABLE `friendship`
(
    `id`            varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `my_id`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自己的用户id',
    `friend_id`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '我朋友的id',
    `friend_remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '好友的备注名',
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
    `friend_remark`  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci                            NOT NULL COMMENT '好友的备注名',
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
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `user_id`     bigint   NOT NULL COMMENT '发朋友圈的用户id',
    `words`       varchar(256)      DEFAULT NULL COMMENT '文字内容',
    `images`      varchar(2560)     DEFAULT NULL COMMENT '图片内容，url用逗号分割',
    `video`       varchar(256)      DEFAULT NULL COMMENT '视频url',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
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
    `id`                   bigint      NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `msg_id`               varchar(64) not null comment '前端生成的消息ID（用于去重和状态同步）',
    `sender_id`            bigint      NOT NULL COMMENT '发送者用户id',
    `receiver_id`          bigint      NOT NULL COMMENT '接收者id（用户或群）',
    `receiver_type`        tinyint     NOT NULL                     DEFAULT 1 COMMENT '接收者类型：1=用户，2=群组',
    `msg`                  varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文本消息内容',
    `msg_type`             tinyint     NOT NULL COMMENT '消息类型：1=文本，2=图片，3=视频，4=语音等',
    `chat_time`            datetime    NOT NULL COMMENT '消息时间（发送/接收时间）',

    `video_cover_url`      varchar(256) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '视频封面地址',
    `video_path`           varchar(256) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '视频地址',
    `video_width`          int                                      DEFAULT NULL COMMENT '视频宽度',
    `video_height`         int                                      DEFAULT NULL COMMENT '视频高度',
    `video_times`          int                                      DEFAULT NULL COMMENT '视频时长（秒）',

    `voice_path`           varchar(256) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '语音地址',
    `speak_voice_duration` int                                      DEFAULT NULL COMMENT '语音时长（秒）',

    `is_read`              tinyint(1)  NOT NULL                     DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
    `is_delete`            tinyint(1)  NOT NULL                     DEFAULT 0 COMMENT '是否删除：0=否，1=是',

    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_sender_receiver_time` (`sender_id`, `receiver_id`, `chat_time`),
    KEY `idx_receiver_time` (`receiver_id`, `chat_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT ='聊天信息存储表';

