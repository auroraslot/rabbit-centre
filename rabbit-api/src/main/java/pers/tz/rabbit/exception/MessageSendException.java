package pers.tz.rabbit.exception;

/**
 * @Author twz
 * @Date 2021-05-10
 * @Desc 发送消息时异常
 */
public class MessageSendException extends MessageRuntimeException {

    private static final long serialVersionUID = -8545460461295084075L;

    public MessageSendException() {
        super();
    }

    public MessageSendException(String desc){
        super(desc);
    }

    public MessageSendException(String desc, Throwable cause) {
        super(desc, cause);
    }

    public MessageSendException(Throwable cause) {
        super(cause);
    }
}
