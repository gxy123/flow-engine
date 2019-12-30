package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.SendResult;
import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.service.impl.EngineMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

import static com.wei.basic.flowengine.client.define.FlowEngineMessageTagDefine.TAG_TASK_CREATED;
import static com.wei.basic.flowengine.client.domain.TaskInstanceDO.STATUS_DOING;
import static org.activiti.engine.delegate.event.ActivitiEventType.TASK_CREATED;

/**
 * 任务创建事件处理器
 * Created by suyaqiang on 2019/1/18.
 */
@Slf4j
@Component
public class TaskCreatedHandler extends MessageSerializationSupport implements EventHandler {

    @Autowired
    private EngineMessageSender messageProducer;

    @Override
    public void handle(ActivitiEvent event) {
        TaskEntity task = (TaskEntity) ((ActivitiEntityEvent) event).getEntity();
        TaskInstanceDO t = new TaskInstanceDO();
        t.setFlowInstanceId(task.getProcessInstanceId());
        t.setName(task.getName());
        t.setId(task.getId());
        t.setProcessDefinitionId(task.getProcessDefinitionId());

        t.setTaskDefinitionKey(task.getTaskDefinitionKey());
        t.setStatus(STATUS_DOING);
        if(StringUtils.isEmpty(task.getProcessDefinitionId())){
            log.error("ProcessDefinitionId_query_empty event={}",event);
            return;
        }
        String defin[] =task.getProcessDefinitionId().split(":");
        if(defin.length!=3){
            log.error("ProcessDefinitionId_query_empty event={}",event);
            return;
        }
        t.setProcessDefinitionKey(defin[0]);
        t.setStartTime(new Date());
        String message = serialize(t);
        SendResult sendResult = messageProducer.buildMessageAndSend(TAG_TASK_CREATED, message);
        log.info("flow_engine_task_create,msgId={},msg={}",sendResult.getMessageId(),message);

    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return TASK_CREATED.equals(event.getType());
    }

}
