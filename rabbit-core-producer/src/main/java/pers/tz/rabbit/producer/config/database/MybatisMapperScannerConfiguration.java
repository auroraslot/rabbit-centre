package pers.tz.rabbit.producer.config.database;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author twz
 * @Date 2021-05-18
 * @Desc $MybatisMapperScannerConfiguration 负责mybatis的mapper接口与xml文件扫描相关的配置
 *          通过@AutoConfigureAfter指定在value后面初始化
 *          是第三步
 */
@Configuration
@AutoConfigureAfter(value = {RabbitProducerDataSourceConfiguration.class})
public class MybatisMapperScannerConfiguration {

    @Bean(name = "rabbitProducerMapperScannerConfigurer")
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setSqlSessionFactoryBeanName("rabbitProducerSqlSessionFactory");
        configurer.setBasePackage("pers.tz.rabbit.producer.mapper");
        return configurer;
    }

}
