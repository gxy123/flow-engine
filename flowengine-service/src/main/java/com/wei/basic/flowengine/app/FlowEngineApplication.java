package com.wei.basic.flowengine.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @version 1.0
 * @author: wangqiaobin
 * @date : 2018/11/13
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableDiscoveryClient
public class FlowEngineApplication implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplication(FlowEngineApplication.class).run(args);
    }

    @Override
    public void run(String... args) {
    }
}
