package com.wei.basic.flowengine.event.listener;

import com.wei.basic.flowengine.event.handler.EventHandler;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by suyaqiang on 2019/1/18.
 */
@Component
public class UnifiedEventListener implements ActivitiEventListener {

    @Resource
    private List<EventHandler> handlers;

    @Override
    public void onEvent(ActivitiEvent event) {
        for (EventHandler h : handlers) {
            if (h.supports(event)) {
                h.handle(event);
                return;
            }
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

}
