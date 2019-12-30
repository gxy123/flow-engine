package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.SendResult;
import com.wei.basic.flowengine.client.domain.ProcessInstanceDO;
import com.wei.basic.flowengine.configer.ServerProperties;
import com.wei.basic.flowengine.service.impl.EngineMessageSender;
import com.wei.client.base.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiProcessStartedEvent;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private EngineMessageSender messageProducer;
    @Autowired
    private ServerProperties mqProperties;

    @Override
    public void handle(ActivitiEvent event) {
        // 此处首先拿到的executionId不是根的
        ExecutionEntity instance = ((ExecutionEntity) ((ActivitiProcessStartedEvent) event).getEntity()).getParent();

        ProcessInstanceDO started = new ProcessInstanceDO();
        started.setProcessDefinitionId(instance.getProcessDefinitionId());
        started.setId(instance.getProcessInstanceId());
        started.setStartTime(instance.getStartTime());
        started.setBusinessKey(instance.getBusinessKey());
        //started.setVariables(instance.getVariables());
        String message = serialize(CommonResult.successReturn(started));

        SendResult sendResult = messageProducer.buildMessageAndSend(TAG_PROCESS_STARTED, message);
        log.info("flow_engine_process_start,msgId={},msg={}",sendResult.getMessageId(),message);
        log.info("send message : topic :{}, tag : {} finished", mqProperties.getProducerTopic(), TAG_PROCESS_STARTED);
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return PROCESS_STARTED.equals(event.getType());
    }
}
