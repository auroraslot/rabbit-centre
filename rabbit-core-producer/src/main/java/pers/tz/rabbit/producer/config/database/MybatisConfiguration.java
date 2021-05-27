package pers.tz.rabbit.producer.config.database;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import pers.tz.rabbit.exception.MessageRuntimeException;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author twz
 * @Date 2021-05-18
 * @Desc $MybatisConfiguration 主要负责Mybatis整合SpringBoot相关配置
 *          通过@AutoConfigureAfter约束必须在value实例化完后才能实例化
 *          是第三步
 */
@Configuration
@AutoConfigureAfter(value = {RabbitProducerDataSourceConfiguration.class})
public class MybatisConfiguration {

    @Resource(name = "rabbitProducerDataSource")
    private DataSource rabbitProducerDataSource;

    /*
        实例化SqlSessionFactory
     */
    @Bean(name = "rabbitProducerSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(rabbitProducerDataSource);
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();

        try {
            bean.setMapperLocations(patternResolver.getResources("classpath:mapping/*.xml"));
            SqlSessionFactory sqlSessionFactory = bean.getObject();
            sqlSessionFactory.getConfiguration().setCacheEnabled(true);
            return sqlSessionFactory;
        } catch (Exception e) {
            throw new MessageRuntimeException(e);
        }
    }


    /*
        SqlSessionTemplate支持事务
     */
    @Bean(name = "rabbitProducerSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
