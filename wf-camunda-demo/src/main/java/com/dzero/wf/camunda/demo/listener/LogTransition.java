package com.dzero.wf.camunda.demo.listener;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.Expression;

/**
 * LogTransition
 *
 * @author DaiZedong
 */
@Slf4j
@Data
public class LogTransition implements ExecutionListener {

    /**
     * 字段注入，字段名称必须与流程里设置的一样
     * https://docs.camunda.org/manual/latest/user-guide/process-engine/delegation-code/#field-injection-on-listener
     */
    private Expression myVariable;

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        log.info("Transition passed with myVariable ExpressionText ={}", myVariable.getExpressionText());
        log.info("Transition passed with myVariable StrValue ={}", myVariable.getValue(delegateExecution).toString());
    }
}
