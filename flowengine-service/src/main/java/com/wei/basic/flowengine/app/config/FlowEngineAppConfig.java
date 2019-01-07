package com.wei.basic.flowengine.app.config;

import org.activiti.cloud.services.events.ProcessEngineChannels;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by suyaqiang on 2018/12/19.
 */
@Configuration
@ComponentScan({
        "com.wei.basic.flowengine",
        "com.wei.service",
        "org.activiti",
})

@EnableBinding({ProcessEngineChannels.class})
public class FlowEngineAppConfig {

}
