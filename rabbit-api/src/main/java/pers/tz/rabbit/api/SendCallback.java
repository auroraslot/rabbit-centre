package pers.tz.rabbit.api;

/**
 * @Author twz
 * @Date 2021-05-12
 * @Desc 消息回调接口
 */
public interface SendCallback {

    /*
        成功回调
     */
    void onSuccess();

    /*
        失败回调
     */
    void onFailure();

}
