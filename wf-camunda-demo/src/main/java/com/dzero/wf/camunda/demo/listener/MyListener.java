package com.dzero.wf.camunda.demo.listener;

import com.dzero.wf.camunda.demo.service.CamundaService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.el.Expression;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricProcessInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.spring.boot.starter.event.ExecutionEvent;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * MyListener
 *
 * @author DaiZedong
 */
@Component
@Slf4j
public class MyListener {
    @Autowired
    private ProcessEngineConfigurationImpl processEngineConfiguration;
    @Autowired
    private CamundaService camundaService;

    @EventListener
    public void onTaskEvent(DelegateTask taskDelegate) {
        // handle mutable task
        if ("assignment".equals(taskDelegate.getEventName())) {
        }
        taskDelegate.getProcessDefinitionId();
        log.info("========= DelegateTask =========" + taskDelegate.getEventName() + "====" + taskDelegate.getProcessInstanceId());
    }

    @EventListener
    public void onTaskEvent(TaskEvent taskEvent) {
        // handle immutable task event
        log.info("========= TaskEvent =========" + taskEvent.getEventName() + "====" + taskEvent.getProcessInstanceId());
    }

    @EventListener
    public void onExecutionEvent(DelegateExecution execution) {
        boolean isStartEvent = execution.getBpmnModelElementInstance() instanceof StartEvent;
        boolean isEndEvent = execution.getBpmnModelElementInstance() instanceof EndEvent;
        ExecutionEntity entity = (ExecutionEntity) execution;
        if (isStartEvent && "start".equals(execution.getEventName()) && entity.getActivityInstanceState() == 0) {
            log.info("onExecutionEvent=====做流程扩展元素");
            // 获取流程扩展属性
            Map<String, Object> exProps = camundaService.getProcessExProperties(execution.getProcessDefinitionId());
            Object exp = exProps.get("loginUser");
            // el表达式解析
            ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();
            Expression expression = expressionManager.createExpression((String) exp);
            expression.getValue(execution);
        }
        log.info("========= DelegateExecution =========" + execution.getEventName() + "====" + execution.getProcessInstanceId());
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
            HistoricActivityInstanceEventEntity activityInstanceEvent = (HistoricActivityInstanceEventEntity) historyEvent;
            if ("userTask".equals(activityInstanceEvent.getActivityType()) &&
                    "start".equals(activityInstanceEvent.getEventType())) {
                log.info("historyEvent");
            }
        }
    }

}
