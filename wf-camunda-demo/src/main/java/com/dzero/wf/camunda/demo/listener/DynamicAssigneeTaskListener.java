package com.dzero.wf.camunda.demo.listener;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

@Slf4j
@Data
public class DynamicAssigneeTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        if ("Activity_14bkfeu".equals(delegateTask.getTaskDefinitionKey())&&"create".equals(delegateTask.getEventName())) {
            delegateTask.setAssignee("dynamicAssignee001");
        }
    }
}
