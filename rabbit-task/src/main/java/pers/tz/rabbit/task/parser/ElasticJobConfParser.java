package pers.tz.rabbit.task.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import pers.tz.rabbit.task.annotation.ElasticJobConfig;
import pers.tz.rabbit.task.autoconfigure.JobZookeeperProperties;
import pers.tz.rabbit.task.enums.ElasticJobTypeEnum;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author twz
 * @Date 2021-05-26
 * @Desc $ElasticJobConfParser 负责解析@ElasticJobConfig 注解
 *      实现$ApplicationListener 接口，实现$onApplicationEvent 方法，确保在spring 容器启动后再执行逻辑
 *      避免混乱
 */
@Slf4j
public class ElasticJobConfParser implements ApplicationListener<ApplicationReadyEvent> {

    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    private JobZookeeperProperties jobZookeeperProperties;

    public ElasticJobConfParser(ZookeeperRegistryCenter zookeeperRegistryCenter, JobZookeeperProperties jobZookeeperProperties) {
        this.zookeeperRegistryCenter = zookeeperRegistryCenter;
        this.jobZookeeperProperties = jobZookeeperProperties;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            ApplicationContext applicationContext = event.getApplicationContext();

            // 1. 找到@ElasticJobConfig 注解标注的类
            // 该注解标注的类是已经被@Component修饰，也就是被spring ioc管理的bean
            Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ElasticJobConfig.class);

            for (Iterator<?> iterator = beanMap.values().iterator(); iterator.hasNext();) {
                Object confBean = iterator.next();
                Class<?> clazz = confBean.getClass();
                String jobClazz = clazz.getName();
                String clazzName = jobClazz;
                if (clazzName.indexOf("$") > 0) {
                    clazz = Class.forName(clazzName.substring(0, clazzName.indexOf("$")));
                }

                // 2. 根据clazz 获取接口类型，判断是什么任务
                // 正常来说可能存在多个接口，需要循环判断
                String jobTypeName = clazz.getInterfaces()[0].getSimpleName();


                // 3. 获取ElasticJobConfig注解中的配置项
                ElasticJobConfig conf = clazz.getAnnotation(ElasticJobConfig.class);

                String namespace = this.jobZookeeperProperties.getNamespace();
                String jobName = namespace + "." + conf.name();
                String cron = conf.cron();
                String shardingItemParameters = conf.shardingItemParameters();
                String description = conf.description();
                String jobParameter = conf.jobParameter();
                String jobExceptionHandler = conf.jobExceptionHandler();
                String executorServiceHandler = conf.executorServiceHandler();
                String jobShardingStrategyClass = conf.jobShardingStrategyClass();
                String eventTraceRdbDataSource = conf.eventTraceRdbDataSource();
                String scriptCommandLine = conf.scriptCommandLine();

                boolean failover = conf.failover();
                boolean misfire = conf.misfire();
                boolean overwrite = conf.overwrite();
                boolean disabled = conf.disabled();
                boolean monitorExecution = conf.monitorExecution();
                boolean streamingProcess = conf.streamingProcess();

                int shardingTotalCount = conf.shardingTotalCount();
                int monitorPort = conf.monitorPort();
                int maxTimeDiffSeconds = conf.maxTimeDiffSeconds();
                int reconcileIntervalMinutes = conf.reconcileIntervalMinutes();

                JobCoreConfiguration coreConfig = JobCoreConfiguration
                        .newBuilder(jobName, cron, shardingTotalCount)
                        .shardingItemParameters(shardingItemParameters)
                        .jobParameter(jobParameter)
                        .failover(failover)
                        .misfire(misfire)
                        .description(description)
                        .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(),
                                executorServiceHandler)
                        .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),
                                jobExceptionHandler)
                        .build();


                // 根据jobTypeName 创建具体类型的任务
                JobTypeConfiguration typeConfig = null;
                if (ElasticJobTypeEnum.SIMPLE.getType().equals(jobTypeName)) {
                    typeConfig = new SimpleJobConfiguration(coreConfig, jobClazz);
                }
                if (ElasticJobTypeEnum.DATAFLOW.getType().equals(jobTypeName)) {
                    typeConfig = new DataflowJobConfiguration(coreConfig, jobClazz, streamingProcess);
                }
                if (ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
                    typeConfig = new ScriptJobConfiguration(coreConfig, scriptCommandLine);
                }

                // LiteJobConfiguration
                LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration
                        .newBuilder(typeConfig)
                        .overwrite(overwrite)
                        .maxTimeDiffSeconds(maxTimeDiffSeconds)
                        .monitorPort(monitorPort)
                        .monitorExecution(monitorExecution)
                        .jobShardingStrategyClass(jobShardingStrategyClass)
                        .disabled(disabled)
                        .reconcileIntervalMinutes(reconcileIntervalMinutes)
                        .build();

                // BeanDefinition的builder， 通过BeanDefinition将SpringJobScheduler注入进去
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
                builder.setInitMethodName("init");
                builder.setScope("prototype");

                if (!ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
                    builder.addConstructorArgValue(confBean); // 真实的任务job
                }
                builder.addConstructorArgValue(this.zookeeperRegistryCenter); // 注册中心
                builder.addConstructorArgValue(liteJobConfiguration); // LiteJobConfiguration

                // eventTraceRdbDataSource 如果有则添加
                if (StringUtils.hasText(eventTraceRdbDataSource)) {
                    BeanDefinitionBuilder rdbBuilder = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
                    rdbBuilder.addConstructorArgReference(eventTraceRdbDataSource);
                    builder.addConstructorArgValue(rdbBuilder.getBeanDefinition());
                }

                // 添加监听BeanDefinition
                List<BeanDefinition> elasticJobListeners = getTargetElasticJobListeners(conf);
                builder.addConstructorArgValue(elasticJobListeners);

                // SpringJobScheduler 的BeanDefinition已经准备好了，将它注入到spring容器中
                // $DefaultListableBeanFactory 是Spring源码层级的核心类之一
                DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
                String beanName = conf.name() + "SpringJobScheduler";
                // 注入spring
                beanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());

                SpringJobScheduler scheduler = applicationContext.getBean(beanName, SpringJobScheduler.class);
                scheduler.init();

                log.info("elastic-job work is started: {}", jobName);
            }

            log.info("elastic-job work started totally is: {}", beanMap.size());
        } catch (ClassNotFoundException e) {
            log.error("elastic-job work starting error, system force exit", e);
            System.exit(1);
        }
    }


    private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConfig listenerConfiguration) {
        List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
        String listeners = listenerConfiguration.listener();
        if (StringUtils.hasText(listeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
            factory.setScope("prototype");
            result.add(factory.getBeanDefinition());
        }

//        Class<? extends ElasticJobListener>[] listeners = listenerConfiguration.clazz();
//        if (null != listeners) {
//            for (Class<? extends ElasticJobListener> listener : listeners) {
//                BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listener);
//                factory.setScope("prototype");
//                result.add(factory.getBeanDefinition());
//            }
//        }

        String distributedListeners = listenerConfiguration.distributedListener();
        long startedTimeoutMilliseconds = listenerConfiguration.startedTimeoutMilliseconds();
        long completedTimeoutMilliseconds = listenerConfiguration.completedTimeoutMilliseconds();

        if (StringUtils.hasText(distributedListeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
            factory.setScope("prototype");
            factory.addConstructorArgValue(startedTimeoutMilliseconds);
            factory.addConstructorArgValue(completedTimeoutMilliseconds);
            result.add(factory.getBeanDefinition());
        }
        return result;
    }

}
