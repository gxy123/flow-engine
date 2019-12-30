package com.wei.basic.flowengine.service.impl;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.fabric.Server;
import com.wei.basic.flowengine.configer.ServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

import static com.wei.common.util.DateUtil.DEFAULT_DATE_FORMAT;

/**
 * @ClassName EngineMessageSender
 * @Author guoxiaoyu
 * @Date 2019/12/2717:44
 **/
@Slf4j
@Component
public class EngineMessageSender {
    @Resource(
            name = "flow-engine-producer"
    )
    private OrderProducerBean producer;
    @Resource
    private ServerProperties properties;

    public SendResult buildMessageAndSend(String tags, Object serializable) {
        Message productMsg = new Message(properties.getProducerTopic(),
                tags,
                serializeBody(serializable));
        SendResult public_sharding_key = producer.send(productMsg, "public_sharding_key");
        log.info("send message of flow-engine finished.messageId:{}",public_sharding_key.getMessageId());
        log.info("send message of flow-engine finished. tags: {}", tags);
        return public_sharding_key;
    }

    private byte[] serializeBody(Object body) {
        if (body instanceof String) {
            return ((String) body).getBytes();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_FORMAT));
        String result;
        try {
            result = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            log.error("message body serialize failed", e);
            throw new RuntimeException("message body serialize failed");
        }
        return result.getBytes();
    }
}
