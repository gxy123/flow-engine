package com.wei.basic.flowengine.web.controller;

import com.wei.basic.flowengine.client.domain.ProcessDefinitionDO;
import com.wei.basic.flowengine.client.domain.ProcessInstanceDO;
import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.service.impl.FlowInstanceService;
import com.wei.client.base.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.cloud.services.api.commands.StartProcessInstanceCmd;
import org.activiti.cloud.services.api.model.ProcessInstance;
import org.activiti.cloud.services.core.ProcessEngineWrapper;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.wei.client.base.CommonResult.successReturn;

/**
 * Created by suyaqiang on 2019/1/7.
 */
@Api(description = "流程实例的相关接口")
@RestController
@RequestMapping("/flowengine/flows/instance")
@Slf4j
public class FlowInstanceController {

    @Resource
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngineWrapper processEngine;
    @Resource
    private HistoryService historyService;
    @Resource
    private FlowInstanceService flowInstanceService;

    @ApiOperation(value = "发起一个流程实例",httpMethod = "POST",notes = "发起一个流程实例")
    @PostMapping("start")
    public CommonResult<ProcessInstanceDO> start(@RequestBody ProcessInstanceDO instanceCmd) {
        StartProcessInstanceCmd cmd = new StartProcessInstanceCmd(instanceCmd.getProcessDefinitionId(),
               null,
                instanceCmd.getVariables(), instanceCmd.getBusinessKey());
        ProcessInstance processInstance = processEngine.startProcess(cmd);
        ProcessInstanceDO instance = new ProcessInstanceDO();
        instance.setBusinessKey(processInstance.getBusinessKey());
        instance.setId(processInstance.getId());
        instance.setStartTime(processInstance.getStartDate());
        return successReturn(instance);
    }
    @ApiOperation(value = "获取某流程实例未完成的节点列表",httpMethod = "GET",notes = "获取某流程实例未完成的节点列表")
    @RequestMapping("todotasks")
    public CommonResult<List<TaskInstanceDO>> completeTask(@RequestParam("id") String id) {
        return successReturn(flowInstanceService.getTodoTasks(id));
    }
    @ApiOperation(value = "根据key获取某流程定义",httpMethod = "GET",notes = "根据key获取某流程定义")
    @RequestMapping(value = "getProcessDefinitionDO",method = RequestMethod.GET)
    public CommonResult<ProcessDefinitionDO> getProcessDefinitionDO(@RequestParam("key") String key) {
        List<ProcessDefinition> list  =repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).orderByProcessDefinitionVersion().desc().list();
        if(!CollectionUtils.isEmpty(list)&&list.size()!=0){
            ProcessDefinition processDef = list.get(0);
            ProcessDefinitionDO defVo = new ProcessDefinitionDO();
            defVo.setId(processDef.getId());
            defVo.setName(processDef.getName());
            defVo.setKey(processDef.getKey());
            defVo.setVersion(processDef.getVersion());
            return CommonResult.successReturn(defVo);
        }
        return CommonResult.errorReturn("未找到该流程！");
    }
}
