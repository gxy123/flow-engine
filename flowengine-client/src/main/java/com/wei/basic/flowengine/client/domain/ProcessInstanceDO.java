package com.wei.basic.flowengine.client.domain;


import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 流程定义 VO
 * Created by suyaqiang on 2019/1/8.
 */
@Data
public class ProcessInstanceDO {

    private String id;

    private String processDefinitionId;

    private String name;

    private String businessKey;

    private Date startDate;

    private Map<String, Object> variables;
}
