package com.dzero.wf.camunda.demo.listener;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

/**
 * 动态设置负责人的任务监听器
 * 对应工作流：user_task_assignee_listener
 */
@Slf4j
@Data
public class DynamicAssigneeTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        if ("Activity_14bkfeu".equals(delegateTask.getTaskDefinitionKey())
                && "create".equals(delegateTask.getEventName())) {
            delegateTask.setAssignee("dynamicAssignee001");
        }
    }
}
