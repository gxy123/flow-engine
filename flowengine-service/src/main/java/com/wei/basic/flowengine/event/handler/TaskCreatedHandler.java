package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.configer.MqProperties;
import com.wei.common.util.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.List;

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
    private RepositoryService repositoryService;

    @PostConstruct
    public void a() {
        repositoryService = ApplicationContextUtil.getBean(RepositoryService.class);
    }


    @Override
    public void handle(ActivitiEvent event) {
        TaskEntity task = (TaskEntity) ((ActivitiEntityEvent) event).getEntity();
        System.out.println("任务创建事件.....");
        TaskInstanceDO t = new TaskInstanceDO();
        t.setFlowInstanceId(task.getProcessInstanceId());
        t.setName(task.getName());
        t.setId(task.getId());
        t.setProcessDefinitionId(task.getProcessDefinitionId());
        t.setTaskDefinitionKey(task.getTaskDefinitionKey());
        t.setStatus("2");
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(t.getProcessDefinitionId()).orderByProcessDefinitionVersion().desc().list();
        if (!CollectionUtils.isEmpty(processDefinitions) && processDefinitions.size() != 0) {
            org.activiti.engine.repository.ProcessDefinition processDefinition = processDefinitions.get(0);
            t.setProcessDefinitionKey(processDefinition.getKey());
        }

        t.setStartTime(task.getCreateTime());
        t.setVariables(task.getVariables());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String message = serialize(t);
        Message m = new Message(mqProperties.getTopic(), "TASK_CREATED", message.getBytes());
        messageProducer.send(m);

        log.info("send message : topic :{}, tag : {} finished", mqProperties.getTopic(), "TASK_CREATED");
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return TASK_CREATED.equals(event.getType());
    }

}
