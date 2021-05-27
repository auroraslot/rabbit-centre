package pers.tz.rabbit.producer.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;
import pers.tz.rabbit.api.Message;

/**
 * @author
 *
 */
@Data
@Builder
public class BrokerMessage implements Serializable {
    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 消息体
     */
    private Message message;

    /**
     * 最大努力尝试次数
     */
    private Integer tryCount;

    /**
     * 消息状态
     */
    private String status;

    /**
     * 下一次重试的时间
     */
    private Date nextRetry;

    /**
     * 第一次入库时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}