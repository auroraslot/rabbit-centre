package pers.tz.rabbit.api;

import org.springframework.util.StringUtils;
import pers.tz.rabbit.exception.MessageRuntimeException;
import pers.tz.rabbit.exception.MessageSendException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author twz
 * @Date 2021-05-10
 * @Desc Message实体的Builder
 *      建造者模式
 */
public class MessageBuilder {

    private String messageId;
    private String topic;
    private String routingKey = "";
    private Map<String, Object> attrs = new HashMap<>();
    private int delayMills;
    private String messageType = MessageType.CONFIRM;

    private MessageBuilder() {
    }

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public MessageBuilder messageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public MessageBuilder topic(String topic) {
        this.topic = topic;
        return this;
    }

    public MessageBuilder routingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }

    public MessageBuilder attrs(Map<String, Object> attr) {
        this.attrs = attr;
        return this;
    }

    public MessageBuilder attr(String key, Object value) {
        this.attrs.put(key, value);
        return this;
    }

    public MessageBuilder delayMills(int delayMills) {
        this.delayMills = delayMills;
        return this;
    }

    public MessageBuilder messageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    public Message build() {

        if (messageId == null) {
            messageId = UUID.randomUUID().toString();
        }

        if (StringUtils.isEmpty(topic)) {
            throw new RuntimeException("topic is empty");
        }

        return new Message(messageId, topic, routingKey, attrs, delayMills, messageType);
    }



}
