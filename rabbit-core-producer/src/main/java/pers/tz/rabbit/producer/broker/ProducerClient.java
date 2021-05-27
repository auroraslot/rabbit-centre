package pers.tz.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.tz.rabbit.api.*;

import java.util.List;

/**
 * @Author twz
 * @Date 2021-05-12
 * @Desc 发送消息的一种实现
 */
@Component
public class ProducerClient implements MessageProducer {

    @Autowired
    private RabbitBroker rabbitBroker;

    @Override
    public void send(Message message) {
        Preconditions.checkNotNull(message.getTopic());

        String messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.RAPID:
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.reliantSend(message);
                break;
            default:
        }

    }

    @Override
    public void send(List<Message> messageList) {

    }

    @Override
    public void send(Message message, SendCallback callback) {

    }
}
