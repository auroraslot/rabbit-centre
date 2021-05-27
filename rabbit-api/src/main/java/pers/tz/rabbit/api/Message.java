package pers.tz.rabbit.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author twz
 * @Date 2021-05-10
 * @Desc 消息实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    private static final long serialVersionUID = 4152161990014561253L;

    /* 消息ID */
    private String messageId;

    /* 消息的主题 */
    private String topic;

    /* 路由键 */
    private String routingKey = "";

    /* 消息的附加属性 */
    private Map<String, Object> attrs = new HashMap<>();

    /* 延迟消息需要用到的时间属性 */
    private int delayMills;

    /* 消息类型，默认是确认消息 */
    private String messageType = MessageType.CONFIRM;

}
