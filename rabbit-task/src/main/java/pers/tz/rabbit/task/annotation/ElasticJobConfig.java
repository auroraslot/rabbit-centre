package pers.tz.rabbit.task.annotation;

import java.lang.annotation.*;

/**
 * @Author twz
 * @Date 2021-05-26
 * @Desc TODO
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ElasticJobConfig {

    String name();   // job名称

    String cron() default "";

    int shardingTotalCount() default 1;

    String shardingItemParameters() default "";

    String jobParameter() default "";

    boolean failover() default false;  // 故障转移

    boolean misfire() default true; // 错过任务重新执行

    String description() default "";    // job描述

    boolean overwrite() default true;   // 本地配置是否覆盖注册中心配置

    boolean streamingProcess() default false;   // 是否流式执行(无视cron)

    String scriptCommandLine() default "";

    boolean monitorExecution() default false;

    int monitorPort() default -1;	//must

    int maxTimeDiffSeconds() default -1;	//must

    String jobShardingStrategyClass() default "";	//must

    int reconcileIntervalMinutes() default 10;	//must

    String eventTraceRdbDataSource() default "";	//must

    String listener() default "";	//must

    boolean disabled() default false;	//must

    String distributedListener() default "";

    long startedTimeoutMilliseconds() default Long.MAX_VALUE;	//must

    long completedTimeoutMilliseconds() default Long.MAX_VALUE;		//must

    String jobExceptionHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultJobExceptionHandler";

    String executorServiceHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultExecutorServiceHandler";



}
