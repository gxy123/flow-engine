package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.SendResult;
import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.configer.ServerProperties;
import com.wei.basic.flowengine.service.impl.EngineMessageSender;
import com.wei.client.base.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.wei.basic.flowengine.client.define.FlowEngineMessageTagDefine.TAG_TASK_ASSIGNED;
import static org.activiti.engine.delegate.event.ActivitiEventType.TASK_ASSIGNED;

/**
 * 任务分派事件处理器
 * Created by suyaqiang on 2019/1/18.
 */
@Slf4j
@Component
public class TaskAssignedHandler extends MessageSerializationSupport implements EventHandler {

    @Autowired
    private EngineMessageSender messageProducer;
    @Autowired
    private ServerProperties mqProperties;

    @Override
    public void handle(ActivitiEvent event) {
        TaskEntity task = (TaskEntity) ((ActivitiEntityEvent) event).getEntity();

        TaskInstanceDO t = new TaskInstanceDO();
        t.setFlowInstanceId(task.getProcessInstanceId());
        t.setName(task.getName());
        t.setId(task.getId());
        t.setStartTime(task.getCreateTime());
        t.setAssignee(Long.valueOf(task.getAssignee()));

        String message = serialize(CommonResult.successReturn(t));
        SendResult sendResult = messageProducer.buildMessageAndSend(TAG_TASK_ASSIGNED, message);
        log.info("flow_engine_task_assigne,msgId={},msg={}",sendResult.getMessageId(),message);
        log.info("send message : topic :{}, tag : {} finished", mqProperties.getProducerTopic(), TAG_TASK_ASSIGNED);
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return TASK_ASSIGNED.equals(event.getType());
    }

}
