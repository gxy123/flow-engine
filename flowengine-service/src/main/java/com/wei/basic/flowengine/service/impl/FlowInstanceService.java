package com.wei.basic.flowengine.service.impl;

import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

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
            todoTask.setStartDate(t.getStartTime());
            undoTasks.add(todoTask);
        }
        return undoTasks;
    }

}
