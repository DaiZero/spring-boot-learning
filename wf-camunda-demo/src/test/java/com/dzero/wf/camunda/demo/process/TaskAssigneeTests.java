package com.dzero.wf.camunda.demo.process;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TaskAssigneeTest
 *
 * @author DaiZedong
 */
@Slf4j
@SpringBootTest
public class TaskAssigneeTests {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Test
    void task_has_assignee_001() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_001");
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskAssignee("001")
                .singleResult();
        Assert.isTrue(task!=null, "task assignee is not 001");
        runtimeService.deleteProcessInstance(processInstance.getId(),"for test");
    }

    @Test
    void task_assignee_el_user(){
        Map<String,Object> mapVal=new HashMap<>();
        mapVal.put("user1","admin0001");
        mapVal.put("user2","employee001");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_el",mapVal);
        Task task1= taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        taskService.complete(task1.getId());
        Task task2= taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        taskService.complete(task2.getId());
        List<HistoricVariableInstance> valList=  historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .list();
        for (HistoricVariableInstance var : valList)
        {
            log.info("Name is "+var.getName()+" and Value is "+var.getValue());
        }
    }
}

