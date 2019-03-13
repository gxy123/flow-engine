package com.wei.basic.flowengine.web.controller;

import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.service.impl.FlowInstanceService;
import com.wei.client.base.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.cloud.services.api.commands.CompleteTaskCmd;
import org.activiti.cloud.services.core.ProcessEngineWrapper;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wei.client.base.CommonResult.successReturn;

/**
 * Created by suyaqiang on 2019/1/7.
 */
@Api(description = "任务实例的相关接口")
@RestController
@RequestMapping("/flowengine/tasks")
@Slf4j
public class TaskInstanceController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngineWrapper processEngine;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private FlowInstanceService flowInstanceService;


    /**
     * 返回正在进行的tasks
     */
    @ApiOperation(value = "提交任务", httpMethod = "POST", notes = "提交任务")
    @RequestMapping(value="complete",method = RequestMethod.POST)
    public CommonResult<List<TaskInstanceDO>> completeTask(
            @RequestParam("taskId") String taskId,
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

    @ApiOperation(value = "节点改派", httpMethod = "POST", notes = "节点改派")
    @RequestMapping(value = "setAssignee",method = RequestMethod.POST)
    public CommonResult<Boolean> setAssignee(
            @RequestParam("taskId") String taskId, @RequestParam("userId") Long userId) {
        try {
            taskService.setAssignee(taskId, userId.toString());
        } catch (Exception e) {
           log.info("Modification anomaly taskId:{}",taskId);
           return CommonResult.errorReturn("改派异常");
        }
        return successReturn(true);
    }

    @ApiOperation(value = "获取引擎里的任务列表（异常数据处理使用）", httpMethod = "GET", notes = "获取引擎里的任务列表（异常数据处理使用）")
    @RequestMapping(value = "getRunTasks",method = RequestMethod.GET)
    public CommonResult<List<TaskInstanceDO>> getRunTasks(@RequestParam Boolean isrunning) {
        List<TaskInstanceDO> list = new ArrayList<>();
        if(isrunning){
            list=flowInstanceService.getRunTask();
        }else{
            list=flowInstanceService.HistoricTasks();
        }
        return successReturn(list);
    }

}
