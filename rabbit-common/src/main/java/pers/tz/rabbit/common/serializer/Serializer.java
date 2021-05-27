package pers.tz.rabbit.common.serializer;

/**
 * @Author twz
 * @Date 2021-05-17
 * @Desc 序列化接口
 */
public interface Serializer {

    /**
     * 序列化为byte[]
     *
     * @param data 需要序列化的对象
     * @return
     */
    byte[] serializerRaw(Object data);

    /**
     * 序列化为String
     *
     * @param data 需要序列化的对象
     * @return
     */
    String serializer(Object data);

    /**
     * 反序列化为指定的类型
     *
     * @param bytes 字节数组
     * @return
     */
    <T> T deserializer(byte[] bytes);

    /**
     * 反序列化为指定的类型
     *
     * @param string 字符串
     * @return
     */
    <T> T deserializer(String string);

}
