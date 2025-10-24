create table address
(
    aid        varchar(36) not null comment '地址id'
        primary key,
    uid        varchar(50) not null comment '用户id',
    sjr        varchar(50) null comment '收件人',
    phone      varchar(13) null comment '联系电话',
    detail     text        null comment '地址详情',
    is_default tinyint     null comment '是否默认地址'
)
    comment '地址表';

create index fk_address_user
    on address (uid);

create table conversation
(
    cid                    varchar(50)                                          not null comment '会话id'
        primary key,
    oid                    varchar(50)                                          null comment '订单id',
    message_counts         int                        default 0                 not null comment '对话消息总条数',
    create_time            datetime                   default CURRENT_TIMESTAMP null comment '会话创建时间',
    last_message_content   text                                                 null,
    last_message_send_time datetime                   default (now())           null comment '最后一条消息送达时间',
    last_message_read_time datetime                                             null comment '最后一条消息已读时间',
    status                 enum ('OPENING', 'CLOSED') default 'OPENING'         null comment '会话状态'
)
    comment '会话表';

create table message
(
    mid       varchar(50)                                                          not null comment '消息id'
        primary key,
    cid       varchar(50)                                                          null comment '会话id',
    sender    varchar(50)                                                          null comment '发送者',
    receiver  varchar(50)                                                          null comment '接收者',
    type      enum ('TEXT', 'VOICE', 'IMAGE')                                      null comment '消息类型',
    content   longblob                                                             null comment '内容',
    status    enum ('SENDING', 'SENT', 'READ', 'FAILED') default 'SENT'            null comment '消息状态',
    send_time datetime                                   default CURRENT_TIMESTAMP null comment '发送消息时间',
    read_time datetime                                                             null
)
    comment '聊天消息表';

create table `order`
(
    oid           varchar(32)                               not null comment '订单id'
        primary key,
    xdr           varchar(50)                               null comment '下单人',
    create_time   datetime                  default (now()) null comment '下单时间',
    expect_time   datetime                                  null comment '期望送达时间',
    order_type    enum ('E', 'T', 'C', 'P') default 'E'     null comment '订单类型',
    aid           varchar(20)                               null comment '地址id',
    detail        json                                      null comment '订单详细数据',
    amount        decimal(5, 2)                             null comment '金额',
    jdr           varchar(50)                               null comment '接单人',
    accept_time   datetime                                  null,
    complete_time datetime                                  null comment '完成时间',
    status        enum ('D', 'J', 'S', 'C') default 'D'     null comment '订单状态（D：待接单；J：进行中；S：已完成；C：已取消）',
    constraint unique_oid
        unique (oid)
)
    comment '订单表';

create index fk_order_address
    on `order` (aid);

create index fk_order_user_jdr
    on `order` (jdr);

create index fk_order_user_xdr
    on `order` (xdr);

create table school
(
    sid   int auto_increment
        primary key,
    sname varchar(100) not null
)
    comment '学校表';

create table settings
(
    id     int auto_increment
        primary key,
    `key`  varchar(100) null,
    value  text         null,
    remark text         null comment '注释'
)
    comment '系统设置';

create table user
(
    uid      char(36)                                 not null comment '用户ID'
        primary key,
    openid   char(28)                                 null,
    nickname varchar(30)                              null comment '昵称',
    sex      smallint       default (-(1))            null,
    sid      int                                      null,
    phone    varchar(13)                              null comment '手机号',
    avatar   longtext                                 null comment '头像url',
    ctime    datetime       default CURRENT_TIMESTAMP null comment '注册时间',
    balance  decimal(10, 2) default 0.00              null comment '余额',
    constraint unique_openid
        unique (openid),
    constraint unique_uid
        unique (uid),
    constraint chk_amount_non_negative
        check (`balance` >= 0)
)
    comment '用户表';

create table transaction
(
    tid         varchar(50)                                                                not null comment '交易id'
        primary key,
    oid         varchar(50)                                                                null comment '订单id',
    uid         varchar(50)                                                                null comment '用户id',
    amount      decimal(10, 2)                                                             null comment '交易金额',
    type        enum ('RECHARGE', 'WITHDRAWAL', 'ORDER', 'COMPLETE_ORDER', 'CANCEL_ORDER') not null comment '交易类型（充值、提现、下单、完单、取消订单）',
    create_time datetime default (now())                                                   null comment '交易时间',
    constraint transaction_order_oid_fk
        foreign key (oid) references `order` (oid),
    constraint transaction_user_uid_fk
        foreign key (uid) references user (uid),
    constraint ck_amount
        check (`amount` >= 0)
)
    comment '交易记录表';
