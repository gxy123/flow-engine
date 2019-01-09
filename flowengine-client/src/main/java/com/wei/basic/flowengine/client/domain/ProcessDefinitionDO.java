package com.wei.basic.flowengine.client.domain;


import lombok.Data;

/**
 * 流程定义 VO
 * Created by suyaqiang on 2019/1/8.
 */
@Data
public class ProcessDefinitionDO {

    private String id;

    private String name;

    private Integer version;

    private String key;
}
