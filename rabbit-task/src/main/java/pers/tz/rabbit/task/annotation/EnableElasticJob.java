package pers.tz.rabbit.task.annotation;

import org.springframework.context.annotation.Import;
import pers.tz.rabbit.task.autoconfigure.JobProcessorAutoConfiguration;

import javax.jws.soap.InitParam;
import java.lang.annotation.*;

/**
 * @Author twz
 * @Date 2021-05-26
 * @Desc 模块化方式装配注解
 *       主要通过@Import注解声明需要装配的类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(JobProcessorAutoConfiguration.class)
public @interface EnableElasticJob {
}
