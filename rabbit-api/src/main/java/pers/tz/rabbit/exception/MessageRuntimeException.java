package pers.tz.rabbit.exception;

/**
 * @Author twz
 * @Date 2021-05-10
 * @Desc 运行时异常
 */
public class MessageRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -2182597055989752838L;

    public MessageRuntimeException() {
        super();
    }

    public MessageRuntimeException(String desc){
        super(desc);
    }

    public MessageRuntimeException(String desc, Throwable cause) {
        super(desc, cause);
    }

    public MessageRuntimeException(Throwable cause) {
        super(cause);
    }
}
