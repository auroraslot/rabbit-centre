package pers.tz.rabbit.producer.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.tz.rabbit.api.Message;
import pers.tz.rabbit.api.MessageType;
import pers.tz.rabbit.api.RabbitBroker;
import pers.tz.rabbit.producer.entity.BrokerMessage;
import pers.tz.rabbit.producer.service.BrokerMessageService;

/**
 * @Author twz
 * @Date 2021-05-12
 * @Desc TODO
 */
@Component
@Slf4j
public class RabbitBrokerImpl implements RabbitBroker {

    @Autowired
    private BrokerMessageService brokerMessageService;

    @Override
    public void rapidSend(Message message) {
        // 这里的set是为了万无一失
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }

    @Override
    public void confirmSend(Message message) {
        message.setMessageType(MessageType.CONFIRM);
        sendKernel(message);
    }

    @Override
    public void reliantSend(Message message) {
        message.setMessageType(MessageType.RELIANT);
        /*
            在sendKernel之前，需要消息入库
            入库消息状态是已发送，通过nextRetry指定下一次发送的时间
            下一次发送时间到达时，如果入库的消息状态还是已发送，则说明消息有问题，就再次发送
            当消息ack成功时，将入库的消息状态修改为已接收
         */

        // 1. 消息入库，状态为发送中，下次重试时间1分钟之后(方便测试)
        brokerMessageService.insert(message);

        // 2. 真正向rabbitmq发送消息
        sendKernel(message);
    }

    @Override
    public void sendMessages() {

    }

    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;

    /*
        发送消息的核心方法，不对外暴露
        虽然convertAndSend是异步的，但是sendKernel不提交到线程池的话却是同步阻塞的
     */
    private void sendKernel(Message message) {
        AsyncBaseQueue.submit(() -> {
            String routingKey = message.getRoutingKey();
            CorrelationData correlationData =
                    new CorrelationData(String.format("%s#%s", message.getMessageId(), System.currentTimeMillis()));
            RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
            rabbitTemplate.convertAndSend("exchange", routingKey, message, correlationData);
            log.info("#RabbitBrokerImpl.sendKernel# send to rabbitmq, messageId: {}", message.getMessageId());
        });
    }
}
