package pers.tz.rabbit.common.serializer.impl;

import pers.tz.rabbit.common.serializer.Serializer;
import pers.tz.rabbit.common.serializer.SerializerFactory;
import pers.tz.rabbit.api.Message;

/**
 * @Author twz
 * @Date 2021-05-17
 * @Desc TODO
 */
public class JacksonSerializerFactory implements SerializerFactory {
    public static final SerializerFactory INSTANCE = new JacksonSerializerFactory();

    private JacksonSerializerFactory() {}

    @Override
    public Serializer create() {
        return JacksonSerializer.createParametricType(Message.class);
    }
}
