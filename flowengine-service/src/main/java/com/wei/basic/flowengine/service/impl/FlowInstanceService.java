package com.wei.basic.flowengine.service.impl;

import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.client.base.CommonResult;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suyaqiang on 2019/1/9.
 */
@Service
public class FlowInstanceService {

    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;

    @Resource
    private RepositoryService repositoryService;

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
            List<ProcessDefinition> processDefinitions =repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(t.getProcessDefinitionId()).orderByProcessDefinitionVersion().desc().list();
            if(!CollectionUtils.isEmpty(processDefinitions)&&processDefinitions.size()!=0){
                org.activiti.engine.repository.ProcessDefinition processDefinition = processDefinitions.get(0);
                todoTask.setProcessDefinitionKey(processDefinition.getKey());
            }
            undoTasks.add(todoTask);
        }
        return undoTasks;
    }


    /**
     * 获取所有代办的任务
     * @return
     */
    public List<TaskInstanceDO> getRunTask() {
        List<TaskInstanceDO> taskInstanceDOS = new ArrayList<>();
        // 保证幂等
        List<Task> taskList =taskService.createTaskQuery().active().list();
      //  List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().unfinished().list();
        if(!CollectionUtils.isEmpty(taskList)&&taskList.size()!=0){
            for (Task t : taskList) {
                TaskInstanceDO instanceDO = new TaskInstanceDO();
                instanceDO.setId(t.getId());
                instanceDO.setFlowInstanceId(t.getProcessInstanceId());
                instanceDO.setName(t.getName());
                instanceDO.setStartTime(t.getCreateTime());
                instanceDO.setTaskDefinitionKey(t.getTaskDefinitionKey());
                List<ProcessDefinition> processDefinitions =repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(t.getProcessDefinitionId()).orderByProcessDefinitionVersion().desc().list();
                if(!CollectionUtils.isEmpty(processDefinitions)&&processDefinitions.size()!=0){
                    org.activiti.engine.repository.ProcessDefinition processDefinition = processDefinitions.get(0);
                    instanceDO.setProcessDefinitionKey(processDefinition.getKey());
                }

                taskInstanceDOS.add(instanceDO);
            }
        }
        return  taskInstanceDOS;
    }
}
