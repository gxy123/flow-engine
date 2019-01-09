package com.wei.basic.flowengine.web.controller;

import com.wei.basic.flowengine.client.domain.ProcessDefinitionDO;
import com.wei.client.base.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by suyaqiang on 2019/1/7.
 */
@RestController
@RequestMapping("/flowengine/repository")
@Slf4j
public class FlowRepositoryController {

    @Resource
    private RepositoryService repositoryService;


    @PostMapping("publish")
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

}
