package com.wei.basic.flowengine.service;

import com.wei.basic.flowengine.client.domain.TaskInstanceDO;

import java.util.List;

/**
 * Created by suyaqiang on 2019/3/13.
 */
public interface FlowInstanceService {

    List<TaskInstanceDO> getTodoTasks(String instanceId);

    List<TaskInstanceDO> getRunTask();

    List<TaskInstanceDO> HistoricTasks();
}
