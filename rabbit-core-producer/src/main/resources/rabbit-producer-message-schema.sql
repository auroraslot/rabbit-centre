use testdb;

create table if not exists broker_msg
(
  message_id  varchar(128) primary key comment '消息ID',
  message     varchar(4000) comment '消息体',
  try_count   int                default 0 comment '最大努力尝试次数',
  status      varchar(10)        default '' comment '消息状态',
  next_retry  timestamp not null DEFAULT CURRENT_TIMESTAMP comment '下一次重试的时间',
  create_time timestamp not null DEFAULT CURRENT_TIMESTAMP comment '第一次入库时间',
  update_time timestamp not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间'
) engine = innodb
  default charset = utf8mb4;