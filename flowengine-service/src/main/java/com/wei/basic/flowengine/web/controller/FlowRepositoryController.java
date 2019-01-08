package com.wei.basic.flowengine.web.controller;

import com.wei.client.base.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

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
    public CommonResult<Deployment> publish(@RequestParam String name, @RequestParam MultipartFile file) {
        Deployment deploy;
        try {
            deploy = repositoryService.createDeployment()
                    .name(file.getOriginalFilename())
                    .addInputStream(file.getOriginalFilename(), file.getInputStream())
                    .deploy();
        } catch (IOException e) {
            log.error("deploy flow error", e);
            return CommonResult.errorReturn("部署流程定义失败");
        }
        log.info("deploy flow {} success", name);
        return CommonResult.successReturn(deploy);
    }

}
