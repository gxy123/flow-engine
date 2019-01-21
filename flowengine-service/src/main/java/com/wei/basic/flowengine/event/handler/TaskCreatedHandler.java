package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.client.domain.UserTaskDO;
import com.wei.basic.flowengine.configer.RocketMQProperties;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static org.activiti.engine.delegate.event.ActivitiEventType.TASK_CREATED;

/**
 * 任务创建事件处理器
 * Created by suyaqiang on 2019/1/18.
 */
@Slf4j
@Component
public class TaskCreatedHandler extends MessageSerializationSupport implements EventHandler {

    @Resource
    private Producer messageProducer;
    @Resource
    private RocketMQProperties mqProperties;

    @Override
    public void handle(ActivitiEvent event) {
        TaskEntity task = (TaskEntity) ((ActivitiEntityEvent) event).getEntity();

        TaskInstanceDO t = new TaskInstanceDO();
        t.setFlowInstanceId(task.getProcessInstanceId());
        t.setName(task.getName());
        t.setId(task.getId());
        t.setProcessDefinitionId(task.getProcessDefinitionId());
        t.setTaskDefinitionKey(task.getTaskDefinitionKey());
        t.setStartDate(task.getCreateTime());
        t.setVariables(task.getVariables());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String message = "";
        try {
            message = mapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error("serialize fail", e);
        }
        t.setStartTime(task.getCreateTime());

        String message = serialize(t);
        Message m = new Message(mqProperties.getTopic(), "TASK_CREATED", message.getBytes());
        messageProducer.send(m);

        log.info("send message : topic :{}, tag : {} finished", mqProperties.getTopic(), "TASK_CREATED");
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return TASK_CREATED.equals(event.getType());
    }

}
