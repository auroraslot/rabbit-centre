package pers.tz.rabbit.task.autoconfigure;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.tz.rabbit.task.parser.ElasticJobConfParser;

/**
 * 模块化装配入口
 * 是该模块下所有bean实例的注入入口
 * 在@EnableElasitcJob 下通过@Import 导入
 *
 * @Author twz
 * @Date 2021-05-26
 * @Desc 通过@ConditionalOnProperty注解，的prefix以及name属性，以及@EnableConfigurationProperties注解
 *       指定$JobZookeeperProperties的注入条件
 *       配置文件中必须要有elastic.job.zk为前缀的属性，并且要有zookeeper.address、zookeeper.namespace两个属性，才会注入$JobZookeeperProperties
 *       否则就不注入
 *
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "elastic.job.zk", name = {"zookeeper.address", "zookeeper.namespace"})
@EnableConfigurationProperties(JobZookeeperProperties.class)
public class JobProcessorAutoConfiguration {

    /**
     * 将zk注册中心加载到spring容器中
     * 通过initMethod属性指定ZookeeperRegistryCenter初始化之后调用它里面的init方法
     *
     * @param jobZookeeperProperties @EnableConfigurationProperties注入的属性
     * @return $ZookeeperRegistryCenter
     */
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(JobZookeeperProperties jobZookeeperProperties) {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(jobZookeeperProperties.getNamespace(),
                jobZookeeperProperties.getServerLists());
        zookeeperConfiguration.setMaxRetries(jobZookeeperProperties.getMaxRetries());
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(jobZookeeperProperties.getConnectionTimeoutMilliseconds());
        zookeeperConfiguration.setBaseSleepTimeMilliseconds(zookeeperConfiguration.getBaseSleepTimeMilliseconds());
        zookeeperConfiguration.setMaxSleepTimeMilliseconds(zookeeperConfiguration.getMaxSleepTimeMilliseconds());
        zookeeperConfiguration.setDigest(jobZookeeperProperties.getDigest());
        zookeeperConfiguration.setSessionTimeoutMilliseconds(jobZookeeperProperties.getSessionTimeoutMilliseconds());

        log.info("初始化job注册中心成功， zkaddress: {}, namespace: {}", jobZookeeperProperties.getServerLists(),
                jobZookeeperProperties.getNamespace());

        return new ZookeeperRegistryCenter(zookeeperConfiguration);
    }

    public ElasticJobConfParser elasticJobConfParser(ZookeeperRegistryCenter zookeeperRegistryCenter,
                                                     JobZookeeperProperties jobZookeeperProperties) {
        return new ElasticJobConfParser(zookeeperRegistryCenter, jobZookeeperProperties);
    }

}
