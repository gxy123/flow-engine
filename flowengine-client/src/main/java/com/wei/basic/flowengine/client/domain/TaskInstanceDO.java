package com.wei.basic.flowengine.client.domain;


import lombok.Data;

import java.util.Date;

/**
 * 流程定义 VO
 * Created by suyaqiang on 2019/1/8.
 */
@Data
public class TaskInstanceDO extends  MapData {

    private String id;

    private String name;

    private String flowInstanceId;

    private String taskDefinitionKey;

    private String processDefinitionId;

    private Long assignee;

    private String status;

    private Date startTime;

    private Date endTime;

}
