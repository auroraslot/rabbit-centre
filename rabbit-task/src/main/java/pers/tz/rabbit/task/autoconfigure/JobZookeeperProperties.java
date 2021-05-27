package pers.tz.rabbit.task.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @Author twz
 * @Date 2021-05-26
 * @Desc 通过@ConfigurationProperties关联配置文件中的前缀
 *       是Zookeeper的配置信息保存实体类
 *
 */
@ConfigurationProperties(prefix = "elastic.job.zk")
@Data
public class JobZookeeperProperties {

    /** zk 命名空间 **/
    private String namespace;

    /** zk 地址列表 如: host1:2181,host2:2181 **/
    private String serverLists;

    /** zk 连接超时时间 单位：毫秒 **/
    private int connectionTimeoutMilliseconds = 10000;

    /** zk 会话超时时间 单位：毫秒 **/
    private int sessionTimeoutMilliseconds = 60000;

    /** 等待重试的间隔时间的初始值 单位：毫秒 **/
    private int baseSleepTimeMilliseconds = 1000;

    /** 等待重试的间隔时间的最大值 单位：毫秒 **/
    private int maxSleepTimeMilliseconds = 3000;

    /** zk 最大重试次数 **/
    private int maxRetries = 3;

    /** 连接Zookeeper的权限令牌 缺省为不需要权限验证 **/
    private String digest;

}
