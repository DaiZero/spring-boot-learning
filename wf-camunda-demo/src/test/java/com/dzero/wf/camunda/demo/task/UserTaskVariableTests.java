package com.dzero.wf.camunda.demo.task;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
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

    @Test
    void task_test() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_001");
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskAssignee("001")
                .singleResult();
        String taskId = task.getId();

        List<VariableInstance> varList = runtimeService
                .createVariableInstanceQuery()
                .processInstanceIdIn(processInstance.getProcessInstanceId())
                .list();

        Map<String, Object> varMap = new HashMap<>(2);
        varMap.put("test_no", "Test001");
        varMap.put("mm_code", "MM001");
        taskService.complete(taskId, varMap);

        List<HistoricVariableInstance> varHisList = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getProcessInstanceId())
                .list();

        runtimeService.deleteProcessInstance(processInstance.getId(), "for test");
    }
}
