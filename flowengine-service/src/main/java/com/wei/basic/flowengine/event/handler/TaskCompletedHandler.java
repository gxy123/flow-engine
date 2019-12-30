package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.SendResult;
import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.configer.ServerProperties;
import com.wei.basic.flowengine.service.impl.EngineMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static com.wei.basic.flowengine.client.define.FlowEngineMessageTagDefine.TAG_TASK_COMPLETED;
import static org.activiti.engine.delegate.event.ActivitiEventType.HISTORIC_ACTIVITY_INSTANCE_ENDED;

/**
 * 任务完成事件处理器
 * Created by suyaqiang on 2019/1/18.
 */
@Slf4j
@Component
public class TaskCompletedHandler extends MessageSerializationSupport implements EventHandler {

    @Autowired
    private EngineMessageSender messageProducer;
    @Autowired
    private ServerProperties mqProperties;

    @Override
    public void handle(ActivitiEvent event) {
        // 要拿到任务的结束时间，所以使用HistoricActivity
        HistoricActivityInstanceEntity historicInstance = (HistoricActivityInstanceEntity) ((ActivitiEntityEventImpl) event).getEntity();

        if (!"userTask".equals(historicInstance.getActivityType())) {
            return;
        }
        TaskInstanceDO t = new TaskInstanceDO();
        t.setFlowInstanceId(historicInstance.getProcessInstanceId());
        t.setName(historicInstance.getActivityName());
        t.setId(historicInstance.getTaskId());
        t.setStartTime(historicInstance.getStartTime());
        t.setEndTime(historicInstance.getEndTime());
        if(Objects.equals(historicInstance.getDeleteReason(),"halt")){
            log.info("process_halt,flowInstanceId={}",t.getId());
            t.setStatus(TaskInstanceDO.STATUS_HALT);
        }else{
            t.setStatus(TaskInstanceDO.STATUS_FINISHED);
        }
        if(StringUtils.isEmpty(historicInstance.getTaskId())||StringUtils.isEmpty(historicInstance.getEndTime())){
            log.error("TaskId_is_null_or_EndTime_is_null,id={}",historicInstance.getProcessInstanceId());
        }

        String message = serialize(t);
        SendResult sendResult = messageProducer.buildMessageAndSend(TAG_TASK_COMPLETED, message);
        log.info("flow_engine_task_complete,msgId={},msg={}",sendResult.getMessageId(),message);
        log.info("send message : topic :{}, tag : {} finished", mqProperties.getProducerTopic(), TAG_TASK_COMPLETED);
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return HISTORIC_ACTIVITY_INSTANCE_ENDED.equals(event.getType());
    }

}
