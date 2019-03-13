package com.wei.basic.flowengine.service.impl;

import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.service.FlowInstanceService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    public List<TaskInstanceDO> getRunTask() {
        List<TaskInstanceDO> taskInstanceDOS = new ArrayList<>();
        // 保证幂等
        List<Task> taskList = taskService.createTaskQuery().active().list();
        //  List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().unfinished().list();
        if (!CollectionUtils.isEmpty(taskList) && taskList.size() != 0) {
            for (Task t : taskList) {
                TaskInstanceDO instanceDO = new TaskInstanceDO();
                instanceDO.setId(t.getId());
                instanceDO.setFlowInstanceId(t.getProcessInstanceId());
                instanceDO.setName(t.getName());
                instanceDO.setStartTime(t.getCreateTime());
                instanceDO.setTaskDefinitionKey(t.getTaskDefinitionKey());
                instanceDO.setStatus(STATUS_DOING.toString());
                List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(t.getProcessDefinitionId()).orderByProcessDefinitionVersion().desc().list();
                if (!CollectionUtils.isEmpty(processDefinitions) && processDefinitions.size() != 0) {
                    org.activiti.engine.repository.ProcessDefinition processDefinition = processDefinitions.get(0);
                    instanceDO.setProcessDefinitionKey(processDefinition.getKey());
                }

                taskInstanceDOS.add(instanceDO);
            }
        }
        return taskInstanceDOS;
    }

    @Override
    public List<TaskInstanceDO> HistoricTasks() {
        List<TaskInstanceDO> taskInstanceDOList = new ArrayList<>();
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery().finished().list();
        if (!CollectionUtils.isEmpty(tasks) && tasks.size() != 0) {
            for (HistoricTaskInstance t : tasks) {
                TaskInstanceDO instanceDO = new TaskInstanceDO();
                instanceDO.setId(t.getId());
                instanceDO.setFlowInstanceId(t.getProcessInstanceId());
                instanceDO.setName(t.getName());
                instanceDO.setStartTime(t.getCreateTime());
                instanceDO.setTaskDefinitionKey(t.getTaskDefinitionKey());
                instanceDO.setStatus(STATUS_FINISHED.toString());
                List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(t.getProcessDefinitionId()).orderByProcessDefinitionVersion().desc().list();
                if (!CollectionUtils.isEmpty(processDefinitions) && processDefinitions.size() != 0) {
                    org.activiti.engine.repository.ProcessDefinition processDefinition = processDefinitions.get(0);
                    instanceDO.setProcessDefinitionKey(processDefinition.getKey());
                }

                taskInstanceDOList.add(instanceDO);
            }
        }
        return taskInstanceDOList;
    }
}
