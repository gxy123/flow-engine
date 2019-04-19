package com.wei.basic.flowengine.configer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @author: wangqiaobin
 * @date : 2018/4/25
 */
@Data
@Component
@ConfigurationProperties("rocketmq.producer")
public class MqProperties {

    private String groupId;
    private String accessKey;
    private String secretKey;
    private String onsAddress;
    private Integer consumeThreadNumbers;
    private String topic; // 广播消息总线topic

}
