package pers.tz.rabbit.producer.config.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * @Author twz
 * @Date 2021-05-18
 * @Desc $BrokerMessageSQLConfiguration 主要负责将数据库脚本找到并执行
 *          此步骤需要从数据源获取jdbc连接，在第二步
 */
@Configuration
@Slf4j
public class BrokerMessageSQLConfiguration {

    /*
        注入在$RabbitProducerDataSourceConfiguration中初始化的Druid数据源
     */
    @Autowired
    private DataSource rabbitProducerDataSource;

    @Value("classpath:rabbit-producer-message-schema.sql")
    private Resource schemaScript;

    public DataSourceInitializer initializer() {
        log.info("================ rabbitProducerDataSource : {} ================", rabbitProducerDataSource);
        final DataSourceInitializer initializer = new DataSourceInitializer();
        // 通过数据源获取一个jdbc连接
        initializer.setDataSource(rabbitProducerDataSource);
        // 设置绑定了SQL脚本的$DatabasePopulator对象
        initializer.setDatabasePopulator(dataBasePopulator());
        return initializer;
    }


    /**
     * 获取$ResourceDatabasePopulator对象并绑定$Resource脚本执行SQL
     *
     * @return
     */
    private DatabasePopulator dataBasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaScript);
        return populator;
    }


}
