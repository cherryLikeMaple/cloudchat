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

INSERT INTO users (account, nickname, face)
VALUES ('cherry01', 'Cherry', 'https://api.iconify.design/flat-color-icons/portrait.svg'),
       ('maple02', '枫叶君', 'https://api.iconify.design/flat-color-icons/businessman.svg'),
       ('bluecat03', '蓝猫', 'https://api.iconify.design/flat-color-icons/cat.svg'),
       ('sunshine04', '小太阳', 'https://api.iconify.design/flat-color-icons/sun.svg'),
       ('coder05', '程序猿', 'https://api.iconify.design/flat-color-icons/electronics.svg'),
       ('snow06', '小雪', 'https://api.iconify.design/flat-color-icons/snowflake.svg'),
       ('tiger07', '阿虎', 'https://api.iconify.design/flat-color-icons/tiger.svg'),
       ('lemon08', '柠檬茶', 'https://api.iconify.design/flat-color-icons/lemon.svg'),
       ('ocean09', '海盐', 'https://api.iconify.design/flat-color-icons/water.svg'),
       ('fox10', '小狐狸', 'https://api.iconify.design/flat-color-icons/fox.svg');


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
