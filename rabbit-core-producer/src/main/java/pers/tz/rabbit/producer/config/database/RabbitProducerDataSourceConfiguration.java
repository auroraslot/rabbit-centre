package pers.tz.rabbit.producer.config.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @Author twz
 * @Date 2021-05-18
 * @Desc $RabbitProducerDataSourceConfiguration 主要负责读取配置文件(PropertySource指定需要加载哪一个配置文件)
 *          然后生成DataSource，具体是@Value("${rabbit.producer.druid.type}")，即druid数据源
 *          由@ConfigurationProperties指定读取配置文件中的哪些属性作为druid数据源的初始化值
 *
 *          该配置类是rabbit-core-producer的配置类中的第一步，所有配置类的前置条件
 */
@Configuration
@PropertySource({"rabbit-producer-message.properties"})
@Slf4j
public class RabbitProducerDataSourceConfiguration {

    @Value("${rabbit.producer.druid.type}")
    private Class<? extends DataSource> dataSourceType;


    /**
     * ConfigurationProperties 将prefix指定前缀的属性都作用在指定类型的数据源上，进行初始化赋值
     *
     * @return
     * @throws SQLException
     */
    @Bean(name = "rabbitProducerDataSource")
    @Primary
    @ConfigurationProperties(prefix = "rabbit.producer.druid.jdbc")
    public DataSource rabbitProducerDataSource() throws SQLException {
        DataSource rabbitProducerDataSource = DataSourceBuilder.create().type(dataSourceType).build();
        log.info("================ rabbitProducerDataSource : {} ================", rabbitProducerDataSource);
        return rabbitProducerDataSource;
    }

    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    public DataSource primaryDataSource() {
        return primaryDataSourceProperties().initializeDataSourceBuilder().build();
    }

}
