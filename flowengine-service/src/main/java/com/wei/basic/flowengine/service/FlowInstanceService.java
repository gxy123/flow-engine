package com.wei.basic.flowengine.service;

import com.wei.basic.flowengine.client.domain.ProcessInstanceDO;
import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.client.base.CommonResult;
import org.activiti.engine.history.HistoricProcessInstance;

import java.util.Date;
import java.util.List;

/**
 * Created by suyaqiang on 2019/3/13.
 */
public interface FlowInstanceService {

    List<TaskInstanceDO> getTodoTasks(String instanceId);

    CommonResult<List<TaskInstanceDO>> getRunTask(String processDefinitionKey, Date date, int pageNum, int pageSize);

    CommonResult<List<TaskInstanceDO>> HistoricTasks(String processDefinitionKey, Date date,int pageNum,int pageSize);

    CommonResult<List<ProcessInstanceDO>>getProcessInstances(List<String> processInstanceIds);

    ProcessInstanceDO getProcessInstancesById(String processInstanceId);

    TaskInstanceDO getTaskByTaskId(String taskId);
}
