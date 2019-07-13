package com.wei.basic.flowengine.configer;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Properties;

import static com.aliyun.openservices.ons.api.PropertyKeyConst.AccessKey;
import static com.aliyun.openservices.ons.api.PropertyKeyConst.GROUP_ID;
import static com.aliyun.openservices.ons.api.PropertyKeyConst.ONSAddr;
import static com.aliyun.openservices.ons.api.PropertyKeyConst.SecretKey;

@Slf4j
@Configuration
public class MqConfiguration {

    @Autowired
    private MqProperties mqProperties;

    private Properties confProps;

    @Bean
    public Producer messageProducer() {
        confProps.setProperty(GROUP_ID, mqProperties.getGroupId());
        Producer producer = ONSFactory.createProducer(confProps);
        producer.start();
        log.info("rocket mq producer started");
        return producer;
    }

    @PostConstruct
    public void init() {
        confProps = new Properties();
        confProps.setProperty(AccessKey, mqProperties.getAccessKey());
        confProps.setProperty(SecretKey, mqProperties.getSecretKey());
        log.info("rocket mq initialized ...");
    }
}
