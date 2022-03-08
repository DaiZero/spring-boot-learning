package com.dzero.wf.camunda.demo.task;

import com.dzero.wf.camunda.demo.service.CamundaService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserTaskTests
 *
 * @author DaiZedong
 */
@Slf4j
@SpringBootTest
public class UserTaskVariableTests {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private CamundaService camundaService;
    @Autowired
    protected ProcessEngineConfigurationImpl processEngineConfiguration;

    @Test
    void task_test() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_001");
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskAssignee("001")
                .singleResult();
        String taskId = task.getId();
//
//        List<VariableInstance> varList = runtimeService
//                .createVariableInstanceQuery()
//                .processInstanceIdIn(processInstance.getProcessInstanceId())
//                .list();

        Map<String, Object> varMap = new HashMap<>(2);
        varMap.put("test_no", "Test001");
        varMap.put("mm_code", "MM001");
        taskService.setVariables(taskId, varMap);
        taskService.complete(taskId);

        List<HistoricVariableInstance> varHisList = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getProcessInstanceId())
                .list();
        for (HistoricVariableInstance historicVariableInstance : varHisList) {
            log.info(historicVariableInstance.getValue().toString());
        }
    }

    @Test
    void getExProps() {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("user_task_assignee_001")
                .latestVersion()
                .singleResult();
        Map<String, Object> exProps = camundaService.getProcessExProperties(definition.getId());
    }


//    public Object getValue(String id, String exp) {
//        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();
//        Expression expression = expressionManager.createExpression(exp);
////        ExecutionEntity executionEntity = (ExecutionEntity) runtimeService
////                .createProcessInstanceQuery()
////                .processInstanceId(processInstanceId)
////                .singleResult();
//        TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery()
//                .taskId(id)
//                .singleResult();
//        ElValueProvider elValueProvider = new ElValueProvider(expression);
//        return elValueProvider.getValue(taskEntity);
////        return expression.getValue(taskEntity);
//    }
}