package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.wei.basic.flowengine.client.domain.UserTaskDO;
import com.wei.basic.flowengine.configer.RocketMQProperties;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static org.activiti.engine.delegate.event.ActivitiEventType.TASK_ASSIGNED;

/**
 * 任务分派事件处理器
 * Created by suyaqiang on 2019/1/18.
 */
@Slf4j
@Component
public class TaskAssignedHandler extends MessageSerializationSupport implements EventHandler {

    @Resource
    private Producer messageProducer;
    @Resource
    private RocketMQProperties mqProperties;

    @Override
    public void handle(ActivitiEvent event) {
        TaskEntity task = (TaskEntity) ((ActivitiEntityEvent) event).getEntity();

        UserTaskDO t = new UserTaskDO();
        t.setFlowId(task.getProcessInstanceId());
        t.setName(task.getName());
        t.setId(task.getId());
        t.setStartTime(task.getCreateTime());

        String message = serialize(t);
        Message m = new Message(mqProperties.getTopic(), "TASK_ASSIGNED", message.getBytes());
        messageProducer.send(m);

        log.info("send message : topic :{}, tag : {} finished", mqProperties.getTopic(), "TASK_ASSIGNED");
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return TASK_ASSIGNED.equals(event.getType());
    }

}
