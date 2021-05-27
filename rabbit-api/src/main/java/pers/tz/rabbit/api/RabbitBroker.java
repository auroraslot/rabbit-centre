package pers.tz.rabbit.api;

/**
 * @Author twz
 * @Date 2021-05-12
 * @Desc 具体发送消息的接口
 */
public interface RabbitBroker {
    /*
        迅速消息
     */
    void rapidSend(Message message);

    /*
        确认消息
     */
    void confirmSend(Message message);

    /*
        可靠性消息
        通过依靠消息状态落库完成100%可靠
     */
    void reliantSend(Message message);

    /*
        批量消息
     */
    void sendMessages();
}
