package com.wei.basic.flowengine.event.handler;

import org.activiti.engine.delegate.event.ActivitiEvent;

/**
 * Created by suyaqiang on 2019/1/18.
 */
public interface EventHandler {

    void handle(ActivitiEvent event);

    boolean supports(ActivitiEvent event);

}
