package com.dzero.wf.camunda.demo.serviceTask;

import com.dzero.wf.camunda.demo.service.DoItService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;

/**
 * CallExternalService
 *
 * @author DaiZedong
 */
@Slf4j
@Named(value = "CallExternalService")
public class CallExternalService implements JavaDelegate {
    @Autowired
    private DoItService doItService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        if (doItService.doIt()) {
            log.info("External service 调用成功！");
        }
    }
}
