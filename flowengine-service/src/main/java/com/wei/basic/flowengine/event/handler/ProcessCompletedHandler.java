package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.wei.basic.flowengine.client.domain.ProcessInstanceDO;
import com.wei.basic.flowengine.configer.MqProperties;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static org.activiti.engine.delegate.event.ActivitiEventType.HISTORIC_PROCESS_INSTANCE_ENDED;

/**
 * 流程完成事件处理器
 * Created by suyaqiang on 2019/1/18.
 */
@Component
@Slf4j
public class ProcessCompletedHandler extends MessageSerializationSupport implements EventHandler {

    @Resource
    private Producer messageProducer;

    @Resource
    private MqProperties mqProperties;

    @Override
    public void handle(ActivitiEvent event) {
        // 此处首先拿到的executionId不是根的
        HistoricProcessInstanceEntity instance = (HistoricProcessInstanceEntity) ((ActivitiEntityEventImpl) event).getEntity();
        ProcessInstanceDO completed = new ProcessInstanceDO();
        completed.setProcessDefinitionId(event.getProcessDefinitionId());
        completed.setId(event.getProcessInstanceId());
        completed.setBusinessKey(instance.getBusinessKey());
        completed.setEndTime(instance.getEndTime());
        completed.setVariables(instance.getProcessVariables());

        String message = serialize(completed);
        Message m = new Message(mqProperties.getTopic(), "HISTORIC_PROCESS_INSTANCE_ENDED", message.getBytes());
        messageProducer.send(m);

        log.info("send message : topic :{}, tag : {} finished", mqProperties.getTopic(), "HISTORIC_PROCESS_INSTANCE_ENDED");
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return HISTORIC_PROCESS_INSTANCE_ENDED.equals(event.getType());
    }
}
