package pers.tz.rabbit.common.convert;

import com.google.common.base.Preconditions;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @Author twz
 * @Date 2021-05-17
 * @Desc RabbitMessageConverter是GenericMessageConverter的装饰器，与它实现一样的接口
 */
public class RabbitMessageConverter implements MessageConverter {

    private GenericMessageConverter genericMessageConverter;

    public RabbitMessageConverter(GenericMessageConverter genericMessageConverter) {
        Preconditions.checkNotNull(genericMessageConverter);

        this.genericMessageConverter = genericMessageConverter;
    }

    // 一天
    private final String delaultExprie = String.valueOf(24 * 60 * 60 * 1000);

    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        messageProperties.setExpiration(delaultExprie);
        messageProperties.setContentEncoding("UTF8");
        // 等等装饰

        return genericMessageConverter.toMessage(o, messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return (pers.tz.rabbit.api.Message) genericMessageConverter.fromMessage(message);
    }
}
