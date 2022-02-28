package com.dzero.wf.camunda.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricProcessInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.spring.boot.starter.event.ExecutionEvent;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * MyListener
 *
 * @author DaiZedong
 */
@Component
@Slf4j
public class MyListener {

//    @EventListener(condition = "#event.eventName=='create'")
//    public void onTaskCreatedEvent(TaskEvent event) {
//        // handle immutable task event
//        log.info("handle task event {} for task id {}", event.getEventName(), event.getId());
//    }
//
//    @EventListener(condition = "#event.eventName=='complete'")
//    public void onTaskCompletedEvent(TaskEvent event) {
//        // handle immutable task event
//        log.info("handle task event {} for task id {}", event.getEventName(), event.getId());
//    }

    @EventListener
    public void onTaskEvent(DelegateTask taskDelegate) {
        // handle mutable task
        log.info("========= DelegateTask =========" + taskDelegate.getEventName() + "====" + taskDelegate.getProcessInstanceId());
    }

    @EventListener
    public void onTaskEvent(TaskEvent taskEvent) {
        // handle immutable task event
        log.info("========= TaskEvent =========" + taskEvent.getEventName() + "====" + taskEvent.getProcessInstanceId());
    }

    @EventListener
    public void onExecutionEvent(DelegateExecution executionDelegate) {
        // handle mutable execution event
        log.info("========= DelegateExecution =========" + executionDelegate.getEventName() + "====" + executionDelegate.getProcessInstanceId());
    }

    @EventListener
    public void onExecutionEvent(ExecutionEvent executionEvent) {
        // handle immutable execution event
        log.info("========= ExecutionEvent =========" + executionEvent.getEventName() + "====" + executionEvent.getProcessInstanceId());
    }

    @EventListener
    public void onHistoryEvent(HistoryEvent historyEvent) {
        if (historyEvent instanceof HistoricProcessInstanceEventEntity) {
            HistoricProcessInstanceEventEntity event = (HistoricProcessInstanceEventEntity) historyEvent;
            switch (event.getState()) {
                case "ACTIVE": {
                    log.info("========= 流程激活 ===== " + event.getEventType() + " ====== " + event.getProcessInstanceId());
                }
                break;
                case "COMPLETED": {
                    log.info("========= 流程完成 ===== " + event.getEventType() + " ====== " + event.getProcessInstanceId());
                }
                break;
                default:
                    break;
            }
        } else if (historyEvent instanceof HistoricActivityInstanceEventEntity) {
            HistoricActivityInstanceEventEntity activityInstanceEvent = (HistoricActivityInstanceEventEntity)historyEvent;
            if ("userTask".equals(activityInstanceEvent.getActivityType()) && "start".equals(activityInstanceEvent.getEventType())) {
                log.info("historyEvent");
            }
        }
    }

}
