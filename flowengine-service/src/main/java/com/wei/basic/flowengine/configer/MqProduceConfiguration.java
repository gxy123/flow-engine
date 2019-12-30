package com.wei.basic.flowengine.configer;

import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.wei.mq.configer.BaseMqProducerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @ClassName MqProduceConfiguration
 * @Author guoxiaoyu
 * @Date 2019/7/617:09
 **/
@Configuration
public class MqProduceConfiguration extends BaseMqProducerConfiguration {
    @Resource
    private ServerProperties properties;
    @Override
    public String groupId() {
        return properties.getGroupId();
    }

    @Bean(
            name = "flow-engine-producer",
            initMethod = "start",
            destroyMethod = "shutdown"
    )
    @Override
    public OrderProducerBean orderProducerBean() {
        return super.orderProducerBean();
    }
}
