package com.wei.basic.flowengine;

import com.wei.basic.flowengine.config.FlowEngineTestConfig;
import org.activiti.engine.RuntimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by suyaqiang on 2019/1/11.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FlowEngineTestConfig.class})
public class ProcessStartEventFT {


    @Resource
    private RuntimeService runtimeService;


    @Test
    public void test() {
        runtimeService.startProcessInstanceById("myProcess:9:5021");

    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
}
