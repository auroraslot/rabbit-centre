package pers.tz.rabbit.api;

/**
 * @Author twz
 * @Date 2021-05-12
 * @Desc 监听消息
 */
public interface MessageListener {

    void onMessage(Message message);

}
