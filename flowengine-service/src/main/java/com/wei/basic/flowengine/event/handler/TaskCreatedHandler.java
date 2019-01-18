package com.wei.basic.flowengine.event.handler;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.springframework.stereotype.Component;

import static org.activiti.engine.delegate.event.ActivitiEventType.TASK_CREATED;

/**
 * Created by suyaqiang on 2019/1/18.
 */
@Component
public class TaskCreatedHandler implements EventHandler {

    @Override
    public void handle(ActivitiEvent event) {
        ((org.activiti.engine.delegate.event.ActivitiEntityEvent) event).getEntity();
        event.getExecutionId();
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return TASK_CREATED.equals(event.getType());
    }
}
