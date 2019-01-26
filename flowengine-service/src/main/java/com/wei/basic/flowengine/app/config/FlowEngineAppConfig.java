package com.wei.basic.flowengine.app.config;

import org.activiti.cloud.services.events.ProcessEngineChannels;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created by suyaqiang on 2018/12/19.
 */
@Configuration
@ComponentScan(basePackages = {
        "com.wei.basic.flowengine",
        "com.wei.service",
        "org.activiti",
})

@EnableBinding({ProcessEngineChannels.class})
public class FlowEngineAppConfig {

    @Bean
    @Primary
    MessageProducerCommandContextCloseListener messageProducerCommandContextCloseListener() {
        return new MessageProducerCommandContextCloseListener(null);
    }


    class MessageProducerCommandContextCloseListener extends org.activiti.cloud.services.events.listeners.MessageProducerCommandContextCloseListener {

        public MessageProducerCommandContextCloseListener(ProcessEngineChannels producer) {
            super(producer);
        }

        @Override
        public void closed(CommandContext commandContext) {
        }

        @Override
        public void closing(CommandContext commandContext) {
        }

        @Override
        public void afterSessionsFlush(CommandContext commandContext) {
        }

        @Override
        public void closeFailure(CommandContext commandContext) {
        }
    }


}
