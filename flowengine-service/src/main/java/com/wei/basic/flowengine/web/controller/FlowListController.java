package com.wei.basic.flowengine.web.controller;

import com.wei.client.base.CommonResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by suyaqiang on 2019/1/7.
 */
@RestController
@RequestMapping("/flowengine/flow")
@Slf4j
public class FlowListController {

    @Resource
    private RepositoryService repositoryService;


    @GetMapping("list")
    public CommonResult list() {
        repositoryService.createDeploymentQuery().list();
        return null;
    }

}
