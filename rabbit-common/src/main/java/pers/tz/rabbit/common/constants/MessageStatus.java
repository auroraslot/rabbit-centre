package pers.tz.rabbit.common.constants;

/**
 * @Author twz
 * @Date 2021-05-18
 * @Desc 消息入库的状态对照
 */
public enum MessageStatus {

    /*
        发送中
     */
    SENDING("0"),

    /*
        已成功接收消息
     */
    SEND_OK("1"),

    /*
        接收消息失败，出现严重状况，不要在重复尝试
     */
    SEND_FAIL("2"),

    /*
        接收消息失败，可以等待一段时间重试
     */
    SEND_FAIL_A_MOUMENT("3");

    private final String code;

    MessageStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
