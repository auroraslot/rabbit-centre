package pers.tz.rabbit.common.convert;

import com.google.common.base.Preconditions;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import pers.tz.rabbit.common.serializer.Serializer;

/**
 * @Author twz
 * @Date 2021-05-17
 * @Desc 序列化，将自己封装的message与spring的message做一个转化
 */
public class GenericMessageConverter implements MessageConverter {
    private Serializer serializer;

    public GenericMessageConverter(Serializer serializer) {
        Preconditions.checkNotNull(serializer);

        this.serializer = serializer;
    }


    /**
     * 写消息
     * 将自己的message转换为amqp规范的message
     *
     * @param o
     * @param messageProperties
     * @return
     * @throws MessageConversionException
     */
    @Override
    public org.springframework.amqp.core.Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(this.serializer.serializerRaw(o), messageProperties);
    }


    /**
     * 读消息
     * 将spring amqp规范的message转化为自己封装的message
     *
     * @param message amqp规范message
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Object fromMessage(org.springframework.amqp.core.Message message) throws MessageConversionException {
        return this.serializer.deserializer(message.getBody());
    }
}
