package com.wei.basic.flowengine.client.domain;


import lombok.Data;

import java.util.Date;

/**
 * 流程定义 VO
 * Created by suyaqiang on 2019/1/8.
 */
@Data
public class TaskInstanceDO extends  MapData {

    /** 进行中 */
    public static final Integer STATUS_DOING = 1;
    /** 暂停 */
    public static final Integer STATUS_PAUSED = 2;
    /** 终止 */
    public static final Integer STATUS_HALT = 3;
    /** 已完成 */
    public static final Integer STATUS_FINISHED = 4;

    private String id;

    private String name;

    private String flowInstanceId;

    private String taskDefinitionKey;

    private String processDefinitionId;

    private String processDefinitionKey;

    private Long assignee;

    private Integer status;

    private Date startTime;

    private Date endTime;


}
