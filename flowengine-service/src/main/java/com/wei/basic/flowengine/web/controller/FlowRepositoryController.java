package com.wei.basic.flowengine.web.controller;

import com.wei.basic.flowengine.client.domain.ProcessDefinitionDO;
import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.client.base.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by suyaqiang on 2019/1/7.
 */
@Api(description = "流程定义相关接口")
@RestController
@RequestMapping("/flowengine/flows")
@Slf4j
public class FlowRepositoryController {

    @Autowired
    private RepositoryService repositoryService;


    @ApiOperation(value = "发布流程接口", httpMethod = "Post", notes = "发布流程接口")
    @RequestMapping(value = "publish",method = RequestMethod.POST)
    public CommonResult<ProcessDefinitionDO> publish(@RequestBody Map<String, String> m) {
        String name = m.get("name");
        String fileString = m.get("fileString");

        Deployment deploy;
        try {
            deploy = repositoryService.createDeployment()
                    .name(name)
                    .addString(name, new String(new Base64().decode(fileString)))
                    .deploy();
        } catch (Exception e) {
            log.error("deploy flow error", e);
            return CommonResult.errorReturn("部署流程定义失败");
        }
        log.info("deploy flow {} success", name);

        ProcessDefinition processDef = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        ProcessDefinitionDO defVo = new ProcessDefinitionDO();
        defVo.setId(processDef.getId());
        defVo.setName(processDef.getName());
        defVo.setKey(processDef.getKey());
        defVo.setVersion(processDef.getVersion());
        return CommonResult.successReturn(defVo);
    }

    @ApiOperation(value = "根据流程ProcessDefinitionId获取该流程的所有节点", httpMethod = "GET", notes = "根据流程ProcessDefinitionId获取该流程的所有节点")
    @RequestMapping(value="tasks",method = RequestMethod.GET)
    public CommonResult<List<TaskInstanceDO>> tasks(@RequestParam("id") String id) {
        List<ProcessDefinition> processDefinitions =repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(id).orderByProcessDefinitionVersion().desc().list();
        if(CollectionUtils.isEmpty(processDefinitions)||processDefinitions.size()==0){
            return  CommonResult.errorReturn("未找到该流程！");
        }
        org.activiti.engine.repository.ProcessDefinition processDefinition = processDefinitions.get(0);

        List<Process> processes = repositoryService.getBpmnModel(id).getProcesses();
        List<TaskInstanceDO> userTasks = new LinkedList<>();
        for (Process process : processes) {
            List<FlowElement> flowElementList = (List<FlowElement>) process.getFlowElements();
            for (FlowElement flowElement : flowElementList) {
                if (flowElement.getClass().equals(UserTask.class)) {
                    UserTask userTask = (UserTask) flowElement;
                    TaskInstanceDO task = new TaskInstanceDO();
                    task.setId(userTask.getId());
                    //task.setTaskDefinitionKey(userTask.getFormKey());
                    task.setName(userTask.getName());
                    task.setFlowInstanceId(id);
                    userTasks.add(task);
                }
            }
        }
        return CommonResult.successReturn(userTasks);
    }

}
