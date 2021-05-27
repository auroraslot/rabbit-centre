package pers.tz.rabbit.exception;

/**
 * @Author twz
 * @Date 2021-05-10
 * @Desc 初始化时异常
 */
public class MessageException extends Exception {
    private static final long serialVersionUID = 3728267681444192206L;

    public MessageException() {
        super();
    }

    public MessageException(String desc){
        super(desc);
    }

    public MessageException(String desc, Throwable cause) {
        super(desc, cause);
    }

    public MessageException(Throwable cause) {
        super(cause);
    }
}
