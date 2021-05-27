package pers.tz.rabbit.producer.service;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.tz.rabbit.api.Message;
import pers.tz.rabbit.common.constants.MessageNextRetryCount;
import pers.tz.rabbit.common.constants.MessageStatus;
import pers.tz.rabbit.producer.entity.BrokerMessage;
import pers.tz.rabbit.producer.mapper.BrokerMessageMapper;

import java.util.Date;

/**
 * @Author twz
 * @Date 2021-05-18
 * @Desc TODO
 */
@Service
public class BrokerMessageService {

    @Autowired
    private BrokerMessageMapper brokerMessageMapper;

    /**
     * 消息入库
     *
     * @param brokerMessage PO
     * @return
     */
    public int insert(BrokerMessage brokerMessage) {
        return brokerMessageMapper.insert(brokerMessage);
    }


    /**
     * 消息入库
     *
     * @param message VO
     * @return
     */
    public int insert(Message message) {
        BrokerMessage brokerMessage = BrokerMessage.builder()
                .messageId(message.getMessageId())
                .message(message)
                .status(MessageStatus.SENDING.getCode())  // 入库状态为：发送中
                .nextRetry(DateUtils.addMinutes(new Date(), MessageNextRetryCount.TIMEOUT_1))     // 方便测试，下次重试时间是1分钟之后
                .build();
        return brokerMessageMapper.insert(brokerMessage);
    }


    /**
     * 成功收到消息，得到正确的ack，根据messageId修改入库的消息状态
     *
     * @param messageId 消息ID
     * @return
     */
    public int success(String messageId) {
        BrokerMessage brokerMessage = BrokerMessage.builder()
                .messageId(messageId)
                .status(MessageStatus.SEND_OK.getCode())
                .build();
        return brokerMessageMapper.updateByPrimaryKey(brokerMessage);
    }

}
