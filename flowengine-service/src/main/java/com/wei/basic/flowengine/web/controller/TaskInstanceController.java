package com.wei.basic.flowengine.web.controller;

import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.service.impl.FlowInstanceService;
import com.wei.client.base.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.activiti.cloud.services.api.commands.CompleteTaskCmd;
import org.activiti.cloud.services.core.ProcessEngineWrapper;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.wei.client.base.CommonResult.successReturn;

/**
 * Created by suyaqiang on 2019/1/7.
 */
@RestController
@RequestMapping("/flowengine/tasks")
@Slf4j
public class TaskInstanceController {

    @Resource
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngineWrapper processEngine;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private FlowInstanceService flowInstanceService;


    /**
     * 返回正在进行的tasks
     */
    @PostMapping("{taskId}/complete")
    public CommonResult<List<TaskInstanceDO>> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {

        // 保证幂等
        HistoricTaskInstance todo = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .unfinished()
                .singleResult();
        if (null != todo) {
            processEngine.completeTask(new CompleteTaskCmd(taskId, variables));
        }
        String processInstanceId = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult().getProcessInstanceId();

        return successReturn(flowInstanceService.getTodoTasks(processInstanceId));
    }

}
