package com.dzero.wf.camunda.demo.serviceTask;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Slf4j
public class LogToConsole implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String pubVariable = (String) delegateExecution.getVariable("pubVariable");
        log.info("参数 pubVariable={}", pubVariable);
        pubVariable = pubVariable + "-postfix";
        delegateExecution.setVariable("pubVariable", pubVariable);
    }
}
