package pers.tz.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pers.tz.rabbit.common.convert.GenericMessageConverter;
import pers.tz.rabbit.common.convert.RabbitMessageConverter;
import pers.tz.rabbit.common.serializer.Serializer;
import pers.tz.rabbit.common.serializer.impl.JacksonSerializerFactory;
import pers.tz.rabbit.api.Message;
import pers.tz.rabbit.api.MessageType;
import pers.tz.rabbit.exception.MessageRuntimeException;
import pers.tz.rabbit.producer.service.BrokerMessageService;

import java.util.List;
import java.util.Map;

/**
 * @Author twz
 * @Date 2021-05-17
 * @Desc rabbittemplate 池化
 */
@Component
@Slf4j
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback {

    private Map<String /* TOPIC */, RabbitTemplate> map = Maps.newConcurrentMap();

    @Autowired
    private ConnectionFactory connectionFactory;

    private Serializer serializer = JacksonSerializerFactory.INSTANCE.create();

    private Splitter splitter = Splitter.on("#");

    @Autowired
    private BrokerMessageService brokerMessageService;


    /**
     * 由于@Autowired直接注入的RabbitTemplate是单例
     * 所以从池化RabbitTemplate中根据TOPIC获取RabbitTemplate，没有则重新创建一个
     *
     * @param message message
     * @return 根据message.topic获取到的RabbitTemplate
     */
    RabbitTemplate getTemplate(Message message) {
        Preconditions.checkNotNull(message);

        RabbitTemplate rabbitTemplate = map.get(message.getTopic());

        if (rabbitTemplate != null) {
            return rabbitTemplate;
        }

        log.info("#RabbitTemplateContainer.getTemplate# topic: {} is exists, create new rabbittemplate", message.getTopic());

        RabbitTemplate newRabbitTemplate = new RabbitTemplate(connectionFactory);

        newRabbitTemplate.setExchange(message.getTopic());
        newRabbitTemplate.setRoutingKey(message.getRoutingKey());
        newRabbitTemplate.setRetryTemplate(new RetryTemplate());

        MessageConverter converter = new RabbitMessageConverter(new GenericMessageConverter(serializer));

        // message的序列化及反序列化
        newRabbitTemplate.setMessageConverter(converter);

        if (!MessageType.RAPID.equals(message.getMessageType())) {
            newRabbitTemplate.setConfirmCallback(this);
        }

        map.putIfAbsent(message.getTopic(), newRabbitTemplate);

        return newRabbitTemplate;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        if (StringUtils.isEmpty(correlationData.getId())) throw new MessageRuntimeException();

        List<String> list = splitter.splitToList(correlationData.getId());
        String messageId = list.get(0);
        String sendTime = list.get(1);

        // 除了true或false，还有一种可能是没有收到ack
        if (ack) {
            // ack是成功的，说明已经收到了消息，将根据messageId将入库的消息状态修改为SEND_OK
            brokerMessageService.success(messageId);
            log.info("send message is OK, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        } else {
            // ack是失败的，说明没有收到消息，根据cause做出不同的处理
            // 比如cause是磁盘爆了，再怎么重试也没有意义，而如果是网络堵塞，则可以nextRetry

            log.error("send message is Fail, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        }
    }
}
