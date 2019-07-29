package com.wei.basic.flowengine.web.controller;

import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.service.FlowInstanceService;
import com.wei.client.base.CommonResult;
import com.wei.common.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.wei.basic.flowengine.client.domain.TaskInstanceDO.STATUS_FINISHED;
import static com.wei.basic.flowengine.client.domain.TaskInstanceDO.STATUS_HALT;
import static com.wei.client.base.CommonResult.successReturn;
import static com.wei.common.util.DateUtil.DAY_FORMAT;
import static com.wei.common.util.DateUtil.DEFAULT_DATE_FORMAT;

/**
 * Created by suyaqiang on 2019/1/7.
 */
@Api(description = "任务实例的相关接口")
@RestController
@RequestMapping("/flowengine/tasks")
@Slf4j
public class TaskInstanceController {

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
    @RequestMapping(value = "complete", method = RequestMethod.POST)
    public CommonResult<TaskInstanceDO> completeTask(
            @RequestParam("taskId") String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {

        // 保证幂等
        HistoricTaskInstance todo = null;
        try {
            todo = historyService.createHistoricTaskInstanceQuery()
                    .taskId(taskId)
                    .unfinished()
                    .singleResult();
        } catch (Exception e) {
            log.error("task_complete_error_msg={},taskId={}",e.getMessage(),taskId);
            return CommonResult.errorReturn("引擎操作异常！");
        }
        if (null == todo) {
            log.error("task_complete_error_not_find_data,taskId={}",taskId);
            return CommonResult.errorReturn("引擎中未找到该任务！");
        }
        try {
            taskService.complete(taskId, variables);
        } catch (Exception e) {
           log.error("task_complete_exception,taskId={},msg={}",taskId,e.getMessage());
           return CommonResult.errorReturn("引擎处理异常！");
        }
       // String processInstanceId = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
             //   .singleResult().getProcessInstanceId();
        HistoricTaskInstance t = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult();
        TaskInstanceDO instanceDO = new TaskInstanceDO();
        instanceDO.setId(t.getId());
        instanceDO.setFlowInstanceId(t.getProcessInstanceId());
        instanceDO.setName(t.getName());
        instanceDO.setStartTime(t.getCreateTime());
        instanceDO.setTaskDefinitionKey(t.getTaskDefinitionKey());
        if(Objects.equals(t.getDeleteReason(),"halt")){
            log.info("process_halt,flowInstanceId={}",t.getId());
            instanceDO.setStatus(TaskInstanceDO.STATUS_HALT);
        }else{
            instanceDO.setStatus(TaskInstanceDO.STATUS_FINISHED);
        }
        instanceDO.setStatus(STATUS_FINISHED);
        instanceDO.setVariables(t.getTaskLocalVariables());
        instanceDO.setProcessDefinitionId(t.getProcessDefinitionId());
        instanceDO.setEndTime(t.getEndTime());
        if (!StringUtils.isEmpty(t.getAssignee())) {
            instanceDO.setAssignee(Long.valueOf(t.getAssignee()));
        }
        if (!StringUtils.isEmpty(t.getProcessDefinitionId())) {
            instanceDO.setProcessDefinitionKey(t.getProcessDefinitionId().split(":")[0]);
        }
        return successReturn(instanceDO);
    }

    @ApiOperation(value = "节点改派", httpMethod = "POST", notes = "节点改派")
    @RequestMapping(value = "setAssignee", method = RequestMethod.POST)
    public CommonResult<Boolean> setAssignee(
            @RequestParam("taskId") String taskId, @RequestParam("userId") Long userId) {
        try {
            taskService.setAssignee(taskId, userId.toString());
        } catch (Exception e) {
            log.error("assignee_exception, taskId:{},msg:{}", taskId,e.getMessage());
            return CommonResult.errorReturn("改派异常");
        }
        return successReturn(true);
    }

    @ApiOperation(value = "获取引擎里的任务列表（异常数据处理使用）", httpMethod = "GET", notes = "获取引擎里的任务列表（异常数据处理使用）")
    @RequestMapping(value = "getRunTasks", method = RequestMethod.GET)
    public CommonResult<List<TaskInstanceDO>> getRunTasks(@RequestParam String processDefinitionKey , @RequestParam String date, @RequestParam Boolean isrunning, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        Date date1 = DateUtil.str2Date(date,DAY_FORMAT);
        if (isrunning) {
            return flowInstanceService.getRunTask(processDefinitionKey,date1,pageNum,pageSize);
        } else {
            return flowInstanceService.HistoricTasks(processDefinitionKey,date1,pageNum,pageSize);
        }
    }

}
