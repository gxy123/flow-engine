package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.basic.flowengine.client.domain.ProcessInstanceDO;
import com.wei.basic.flowengine.configer.MqProperties;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiProcessStartedEvent;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

import static com.wei.basic.flowengine.client.define.FlowEngineMessageTagDefine.TAG_PROCESS_STARTED;
import static org.activiti.engine.delegate.event.ActivitiEventType.PROCESS_STARTED;

/**
 * 流程启动事件处理器
 * Created by suyaqiang on 2019/1/18.
 */
@Component
@Slf4j
public class ProcessStartedHandler extends MessageSerializationSupport implements EventHandler {

    @Autowired
    private Producer messageProducer;

    @Autowired
    private MqProperties mqProperties;

    @Override
    public void handle(ActivitiEvent event) {
        // 此处首先拿到的executionId不是根的
        ExecutionEntity instance = ((ExecutionEntity) ((ActivitiProcessStartedEvent) event).getEntity()).getParent();

        ProcessInstanceDO started = new ProcessInstanceDO();
        started.setProcessDefinitionId(event.getProcessDefinitionId());
        started.setId(event.getProcessInstanceId());
        started.setStartTime(instance.getStartTime());
        started.setBusinessKey(instance.getBusinessKey());
        started.setVariables(instance.getVariables());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String message = serialize(started);
        Message m = new Message(mqProperties.getTopic(), TAG_PROCESS_STARTED, message.getBytes());
        messageProducer.send(m);

        log.info("send message : topic :{}, tag : {} finished", mqProperties.getTopic(), TAG_PROCESS_STARTED);
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return PROCESS_STARTED.equals(event.getType());
    }
}
