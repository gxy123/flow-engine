package com.wei.basic.flowengine.service.impl;

import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.service.FlowInstanceService;
import com.wei.client.base.CommonResult;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.wei.basic.flowengine.client.domain.TaskInstanceDO.STATUS_DOING;
import static com.wei.basic.flowengine.client.domain.TaskInstanceDO.STATUS_FINISHED;

/**
 * Created by suyaqiang on 2019/1/9.
 */
@Service
public class FlowInstanceServiceImpl implements FlowInstanceService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public List<TaskInstanceDO> getTodoTasks(String instanceId) {

        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(instanceId)
                .unfinished()
                .list();
        List<TaskInstanceDO> undoTasks = new ArrayList<>();
        for (HistoricTaskInstance t : tasks) {
            TaskInstanceDO todoTask = new TaskInstanceDO();
            todoTask.setId(t.getId());
            todoTask.setFlowInstanceId(t.getProcessInstanceId());
            todoTask.setName(t.getName());
            todoTask.setStartTime(t.getStartTime());
            todoTask.setTaskDefinitionKey(t.getTaskDefinitionKey());
            todoTask.setProcessDefinitionId(t.getProcessDefinitionId());
            List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(t.getProcessDefinitionId()).orderByProcessDefinitionVersion().desc().list();
            if (!CollectionUtils.isEmpty(processDefinitions) && processDefinitions.size() != 0) {
                org.activiti.engine.repository.ProcessDefinition processDefinition = processDefinitions.get(0);
                todoTask.setProcessDefinitionKey(processDefinition.getKey());
            }
            undoTasks.add(todoTask);
        }
        return undoTasks;
    }


    /**
     * 获取所有代办的任务
     *
     * @return
     */
    @Override
    public CommonResult<List<TaskInstanceDO>> getRunTask(String processDefinitionKey, int pageNum, int pageSize) {
        CommonResult<List<TaskInstanceDO>> commonResult = new CommonResult<>();
        List<TaskInstanceDO> taskInstanceDOS = new ArrayList<>();
        // 保证幂等
        Long count = historyService.createHistoricTaskInstanceQuery().unfinished().processDefinitionKey(processDefinitionKey).count();
        commonResult.setTotal(Integer.valueOf(count.toString()));
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().unfinished().processDefinitionKey(processDefinitionKey).listPage(pageNum, pageSize);
        if (!CollectionUtils.isEmpty(list) && list.size() != 0) {
            for (HistoricTaskInstance t : list) {
                TaskInstanceDO instanceDO = new TaskInstanceDO();
                instanceDO.setId(t.getId());
                instanceDO.setFlowInstanceId(t.getProcessInstanceId());
                instanceDO.setName(t.getName());
                instanceDO.setStartTime(t.getCreateTime());
                instanceDO.setTaskDefinitionKey(t.getTaskDefinitionKey());
                instanceDO.setVariables(t.getTaskLocalVariables());
                instanceDO.setProcessDefinitionId(t.getProcessDefinitionId());
                if (!StringUtils.isEmpty(t.getAssignee())) {
                    instanceDO.setAssignee(Long.valueOf(t.getAssignee()));
                }

                instanceDO.setStatus(STATUS_DOING);
                if (!StringUtils.isEmpty(t.getProcessDefinitionId())) {
                    instanceDO.setProcessDefinitionKey(t.getProcessDefinitionId().split(":")[0]);
                }


                taskInstanceDOS.add(instanceDO);
            }
            commonResult.setResult(taskInstanceDOS);
            commonResult.setSuccess(true);
        }
        return commonResult;
    }

    @Override
    public CommonResult<List<TaskInstanceDO>> HistoricTasks(String processDefinitionKey, int pageNum, int pageSize) {
        CommonResult<List<TaskInstanceDO>> commonResult = new CommonResult<>();
        List<TaskInstanceDO> taskInstanceDOList = new ArrayList<>();
        Long count = historyService.createHistoricTaskInstanceQuery().finished().processDefinitionKey(processDefinitionKey).count();
        commonResult.setTotal(Integer.valueOf(count.toString()));
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery().finished().processDefinitionKey(processDefinitionKey).listPage(pageNum, pageSize);
        if (!CollectionUtils.isEmpty(tasks) && tasks.size() != 0) {
            for (HistoricTaskInstance t : tasks) {
                TaskInstanceDO instanceDO = new TaskInstanceDO();
                instanceDO.setId(t.getId());
                instanceDO.setFlowInstanceId(t.getProcessInstanceId());
                instanceDO.setName(t.getName());
                instanceDO.setStartTime(t.getCreateTime());
                instanceDO.setTaskDefinitionKey(t.getTaskDefinitionKey());
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

                taskInstanceDOList.add(instanceDO);
            }
            commonResult.setResult(taskInstanceDOList);
        }
        return commonResult;
    }
}
