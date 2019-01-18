package com.wei.basic.flowengine.event.handler;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.springframework.stereotype.Component;

import static org.activiti.engine.delegate.event.ActivitiEventType.PROCESS_STARTED;

/**
 * Created by suyaqiang on 2019/1/18.
 */
@Component
public class ProcessStartedHandler implements EventHandler {

    @Override
    public void handle(ActivitiEvent event) {
        event.getProcessDefinitionId();
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return PROCESS_STARTED.equals(event.getType());
    }
}
