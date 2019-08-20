package com.wei.basic.flowengine.config;

import com.wei.basic.flowengine.app.config.FlowEngineAppConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by suyaqiang on 2019/1/11.
 */

@Import(FlowEngineAppConfig.class)
@Configuration
public class FlowEngineTestConfig {


}
