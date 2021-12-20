package com.dzero.wf.camunda.demo.process;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.DelegateExecution;
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
    @EventListener
    public void onTaskEvent(DelegateTask taskDelegate) {
        // handle mutable task
        log.info("onTaskEvent-DelegateTask" + taskDelegate.getProcessDefinitionId());
    }

    @EventListener
    public void onTaskEvent(TaskEvent taskEvent) {
        // handle immutable task event
        log.info("onTaskEvent-TaskEvent" + taskEvent.getProcessDefinitionId());
    }

    @EventListener
    public void onExecutionEvent(DelegateExecution executionDelegate) {
        // handle mutable execution event
        log.info("onExecutionEvent-DelegateExecution" + executionDelegate.getProcessDefinitionId());
    }

    @EventListener
    public void onExecutionEvent(ExecutionEvent executionEvent) {
        // handle immutable execution event
        log.info("onExecutionEvent-ExecutionEvent" + executionEvent.getProcessDefinitionId());
    }

    @EventListener
    public void onHistoryEvent(HistoryEvent historyEvent) {
        // handle history event
        log.info("onHistoryEvent-HistoryEvent" + historyEvent.getProcessDefinitionId());
    }

}
