package com.wei.basic.flowengine.client.domain;


import lombok.Data;

import java.util.Date;

/**
 * 流程定义 VO
 * Created by suyaqiang on 2019/1/8.
 */
@Data
public class UserTaskDO {

    private String id;

    private String name;

    private String flowId;

    private Date startTime;

    private Date endTime;

}
