package com.wei.basic.flowengine.web.controller;

import com.wei.basic.flowengine.client.domain.ProcessDefinitionDO;
import com.wei.basic.flowengine.client.domain.ProcessInstanceDO;
import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.service.FlowInstanceService;
import com.wei.client.base.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
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

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private FlowInstanceService flowInstanceService;

    @ApiOperation(value = "发起一个流程实例", httpMethod = "POST", notes = "发起一个流程实例")
    @PostMapping("start")
    public CommonResult<ProcessInstanceDO> start(@RequestBody ProcessInstanceDO instanceCmd) {

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                instanceCmd.getProcessDefinitionId(),
                instanceCmd.getBusinessKey(),
                instanceCmd.getVariables());
        ProcessInstanceDO instance = new ProcessInstanceDO();
        instance.setBusinessKey(processInstance.getBusinessKey());
        instance.setId(processInstance.getId());
        instance.setStartTime(processInstance.getStartTime());

        return successReturn(instance);
    }

    @ApiOperation(value = "强制完成流程实例", httpMethod = "POST", notes = "强制完成流程实例")
    @PostMapping("finish")
    public CommonResult<ProcessInstanceDO> finish(@RequestParam("id") String id, @RequestParam("reason") String reason) {
        try {
            runtimeService.deleteProcessInstance(id, reason);
            return CommonResult.successReturn(flowInstanceService.getProcessInstancesById(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonResult.successReturn(null);
    }

    @ApiOperation(value = "获取某流程实例未完成的节点列表", httpMethod = "GET", notes = "获取某流程实例未完成的节点列表")
    @RequestMapping("todotasks")
    public CommonResult<List<TaskInstanceDO>> completeTask(@RequestParam("id") String id) {
        return successReturn(flowInstanceService.getTodoTasks(id));
    }

    @ApiOperation(value = "根据key获取某流程定义", httpMethod = "GET", notes = "根据key获取某流程定义")
    @RequestMapping(value = "getProcessDefinition", method = RequestMethod.GET)
    public CommonResult<ProcessDefinitionDO> getProcessDefinition(@RequestParam("key") String key) {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).orderByProcessDefinitionVersion().desc().list();
        if (!CollectionUtils.isEmpty(list) && list.size() != 0) {
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

    @ApiOperation(value = "获取流程实例列表根据实例ids", httpMethod = "GET", notes = "获取流程实例列表根据实例ids")
    @RequestMapping("getFlowInstancesByProcInstIds")
    public CommonResult<List<ProcessInstanceDO>> getFlowInstancesByProcInstIds(@RequestParam("ProcInstIds") List<String> ProcInstIds) {
        return flowInstanceService.getProcessInstances(ProcInstIds);
    }
}
