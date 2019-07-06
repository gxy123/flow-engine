package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.configer.MqProperties;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.wei.basic.flowengine.client.define.FlowEngineMessageTagDefine.TAG_TASK_CREATED;
import static com.wei.basic.flowengine.client.domain.TaskInstanceDO.STATUS_DOING;
import static org.activiti.engine.delegate.event.ActivitiEventType.TASK_CREATED;

/**
 * 任务创建事件处理器
 * Created by suyaqiang on 2019/1/18.
 */
@Slf4j
@Component
public class TaskCreatedHandler extends MessageSerializationSupport implements EventHandler {

    @Autowired
    private Producer messageProducer;
    @Autowired
    private MqProperties mqProperties;
    @Autowired
    private HistoryService historyService;

    @Override
    public void handle(ActivitiEvent event) {
        TaskEntity task = (TaskEntity) ((ActivitiEntityEvent) event).getEntity();
        TaskInstanceDO t = new TaskInstanceDO();
        t.setFlowInstanceId(task.getProcessInstanceId());
        t.setName(task.getName());
        t.setId(task.getId());
        t.setProcessDefinitionId(task.getProcessDefinitionId());

        t.setTaskDefinitionKey(task.getTaskDefinitionKey());
        t.setStatus(STATUS_DOING);
        HistoricProcessInstanceQuery historicProcessQuery = historyService.createHistoricProcessInstanceQuery();


        HistoricProcessInstanceQuery historicProcessInstanceQuery = historicProcessQuery.processInstanceId(task.getProcessInstanceId());
        if(CollectionUtils.isEmpty(historicProcessInstanceQuery.list())){
            log.error("historic_process_instance_query_empty event={}",event);
            return;
        }
        HistoricProcessInstance historicProcessInstance = historicProcessInstanceQuery.list().get(0);
        t.setProcessDefinitionKey(historicProcessInstance.getProcessDefinitionKey());
        t.setStartTime(new Date());
        t.setVariables(task.getVariables());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String message = serialize(t);
        Message m = new Message(mqProperties.getTopic(), TAG_TASK_CREATED, message.getBytes());
        messageProducer.send(m);
        log.info("FlowInstanceId:{},ProcessDefinitionKey:{}",task.getProcessInstanceId(),task.getProcessInstance().getProcessDefinitionKey());

        log.info("send message : topic :{}, tag : {} finished", mqProperties.getTopic(), TAG_TASK_CREATED);
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return TASK_CREATED.equals(event.getType());
    }

}
