package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.basic.flowengine.client.domain.ProcessInstanceDO;
import com.wei.basic.flowengine.configer.RocketMQProperties;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiProcessStartedEvent;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

import static org.activiti.engine.delegate.event.ActivitiEventType.PROCESS_COMPLETED;

/**
 * Created by suyaqiang on 2019/1/18.
 */
@Component
@Slf4j
public class ProcessCompletedHandler implements EventHandler {

    @Resource
    private Producer messageProducer;

    @Resource
    private RocketMQProperties mqProperties;

    @Override
    public void handle(ActivitiEvent event) {
        // 此处首先拿到的executionId不是根的
        ExecutionEntity instance = ((ExecutionEntity) ((ActivitiProcessStartedEvent) event).getEntity()).getParent();

        ProcessInstanceDO started = new ProcessInstanceDO();
        started.setProcessDefinitionId(event.getProcessDefinitionId());
        started.setId(event.getProcessInstanceId());
        started.setBusinessKey(instance.getBusinessKey());

        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String message = "";
        try {
            message = mapper.writeValueAsString(started);
        } catch (JsonProcessingException e) {
            log.error("serialize fail", e);
        }
        Message m = new Message(mqProperties.getTopic(), "PROCESS_COMPLETED", message.getBytes());
        messageProducer.send(m);

        log.info("send message : topic :{}, tag : {} finished", mqProperties.getTopic(), "PROCESS_COMPLETED");
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return PROCESS_COMPLETED.equals(event.getType());
    }
}
