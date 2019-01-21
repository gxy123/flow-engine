package com.wei.basic.flowengine;

import com.wei.basic.flowengine.config.FlowEngineTestConfig;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程集成测试
 * Created by suyaqiang on 2019/1/11.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FlowEngineTestConfig.class})
public class ProcessIT {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Test
    public void test() {

        String businessKey = String.valueOf(System.currentTimeMillis());
        Map vs = new HashMap();
        vs.put("customerId", "8987324");
        vs.put("serviceId", "190117000002011");
        vs.put("cityId", "110000");

        String instanceId = runtimeService.startProcessInstanceById("myProcess:1:3",
                businessKey, vs).getProcessInstanceId();

        // 处理任务
        Task task = taskService.createTaskQuery().processInstanceId(instanceId).list().get(0);

        taskService.setAssignee(task.getId(), "小棉袄");
        taskService.complete(task.getId());
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

}
