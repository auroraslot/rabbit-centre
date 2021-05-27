package pers.tz.rabbit.api;

import java.util.List;

/**
 * @Author twz
 * @Date 2021-05-12
 * @Desc 消息发送者接口
 */
public interface MessageProducer {

    /*
        发送消息
     */
    void send(Message message);

    /*
        批量发送消息
     */
    void send(List<Message> messageList);

    /*
        发送消息后有回调
     */
    void send(Message message, SendCallback callback);
}
