package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.wei.basic.flowengine.client.domain.ProcessInstanceDO;
import com.wei.basic.flowengine.configer.MqProperties;
import com.wei.client.base.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.wei.basic.flowengine.client.define.FlowEngineMessageTagDefine.TAG_PROCESS_COMPLETED;
import static org.activiti.engine.delegate.event.ActivitiEventType.HISTORIC_PROCESS_INSTANCE_ENDED;

/**
 * 流程完成事件处理器
 * Created by suyaqiang on 2019/1/18.
 */
@Component
@Slf4j
public class ProcessCompletedHandler extends MessageSerializationSupport implements EventHandler {

    @Autowired
    private Producer messageProducer;

    @Autowired
    private MqProperties mqProperties;

    @Override
    public void handle(ActivitiEvent event) {
        // 此处首先拿到的executionId不是根的
        HistoricProcessInstanceEntity instance = (HistoricProcessInstanceEntity) ((ActivitiEntityEventImpl) event).getEntity();
        ProcessInstanceDO completed = new ProcessInstanceDO();
        completed.setProcessDefinitionId(instance.getProcessDefinitionId());
        completed.setId(instance.getProcessInstanceId());
        completed.setBusinessKey(instance.getBusinessKey());
        completed.setEndTime(instance.getEndTime());
        completed.setVariables(instance.getProcessVariables());
        String message = serialize(CommonResult.successReturn(completed));
        log.info("processInstance_completed_send_msg={}",message);
        Message m = new Message(mqProperties.getTopic(), TAG_PROCESS_COMPLETED, message.getBytes());
        messageProducer.send(m);

    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return HISTORIC_PROCESS_INSTANCE_ENDED.equals(event.getType());
    }
}
