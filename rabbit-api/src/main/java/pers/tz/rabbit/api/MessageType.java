package pers.tz.rabbit.api;

/**
 * @Author twz
 * @Date 2021-05-10
 * @Desc TODO
 */
public final class MessageType {

    /*
        迅速消息，不需要保证消息的可靠性，也不需要做confirm确认
     */
    public static final String RAPID = "0";

    /*
        确认消息，不需要保证消息的可靠性，但是需要confirm确认
     */
    public static final String CONFIRM = "1";

    /*
        可靠消息，必须保证消息的100%可靠性投递，不允许任何消息丢失
        保证数据库和所发的消息是最终一致性的
     */
    public static final String RELIANT = "2";



}
