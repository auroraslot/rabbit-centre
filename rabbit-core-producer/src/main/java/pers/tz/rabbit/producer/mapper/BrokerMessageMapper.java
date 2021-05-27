package pers.tz.rabbit.producer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import pers.tz.rabbit.producer.entity.BrokerMessage;

/**
 * BrokerMessageMapper继承基类
 */
@Mapper
@Repository
public interface BrokerMessageMapper extends MyBatisBaseDao<BrokerMessage, String> {
}