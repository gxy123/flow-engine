package com.wei.basic.flowengine.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by suyaqiang on 2018/12/19.
 */
@Configuration
@ComponentScan(basePackages = {
        "com.wei.common.util",
        "com.wei.basic.flowengine",
        "com.wei.service",
})

public class FlowEngineAppConfig {

}
