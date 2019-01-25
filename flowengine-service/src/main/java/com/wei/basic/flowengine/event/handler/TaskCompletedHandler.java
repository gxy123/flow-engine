package com.wei.basic.flowengine.event.handler;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.wei.basic.flowengine.client.domain.TaskInstanceDO;
import com.wei.basic.flowengine.configer.MqProperties;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static org.activiti.engine.delegate.event.ActivitiEventType.HISTORIC_ACTIVITY_INSTANCE_ENDED;

/**
 * 任务完成事件处理器
 * Created by suyaqiang on 2019/1/18.
 */
@Slf4j
@Component
public class TaskCompletedHandler extends MessageSerializationSupport implements EventHandler {

    @Resource
    private Producer messageProducer;
    @Resource
    private MqProperties mqProperties;

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
        t.setId(historicInstance.getId());
        t.setStartTime(historicInstance.getStartTime());
        t.setEndTime(historicInstance.getEndTime());

        String message = serialize(t);
        Message m = new Message(mqProperties.getTopic(), "HISTORIC_ACTIVITY_INSTANCE_ENDED", message.getBytes());
        messageProducer.send(m);

        log.info("send message : topic :{}, tag : {} finished", mqProperties.getTopic(), "HISTORIC_ACTIVITY_INSTANCE_ENDED");
    }

    @Override
    public boolean supports(ActivitiEvent event) {
        return HISTORIC_ACTIVITY_INSTANCE_ENDED.equals(event.getType());
    }

}
