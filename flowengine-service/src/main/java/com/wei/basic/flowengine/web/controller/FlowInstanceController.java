package com.wei.basic.flowengine.web.controller;

import com.wei.basic.flowengine.client.domain.ProcessInstanceDO;
import com.wei.client.base.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.activiti.cloud.services.api.commands.StartProcessInstanceCmd;
import org.activiti.cloud.services.api.model.ProcessInstance;
import org.activiti.cloud.services.core.ProcessEngineWrapper;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by suyaqiang on 2019/1/7.
 */
@RestController
@RequestMapping("/flowengine/flows")
@Slf4j
public class FlowInstanceController {

    @Resource
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngineWrapper processEngine;


    @PostMapping("start")
    public CommonResult<ProcessInstanceDO> start(@RequestBody ProcessInstanceDO instanceCmd) {
        StartProcessInstanceCmd cmd = new StartProcessInstanceCmd(null, instanceCmd.getProcessDefinitionId()
                , instanceCmd.getVariables(), instanceCmd.getBusinessKey());
        ProcessInstance processInstance = processEngine.startProcess(cmd);
        ProcessInstanceDO instance = new ProcessInstanceDO();
        instance.setBusinessKey(processInstance.getBusinessKey());
        instance.setId(processInstance.getId());
        instance.setStartDate(processInstance.getStartDate());
        return CommonResult.successReturn(instance);
    }

}
