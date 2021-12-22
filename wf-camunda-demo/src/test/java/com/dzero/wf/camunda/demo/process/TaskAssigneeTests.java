package com.dzero.wf.camunda.demo.process;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
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
        // 1. 参数值设置
        Map<String,Object> mapVal=new HashMap<>();
        mapVal.put("user1","admin0001");
        // 2. 开始 流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_el",mapVal);
        // 3. 完成工作流任务
        // 3.1 完成任务1
        Task task1= taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        taskService.complete(task1.getId());
        // 3.2 完成任务2
        Task task2= taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        taskService.complete(task2.getId());
        // 4. 查询工作流的任务历史信息
        // 4.1 任务1的负责人是变量赋值
        HistoricTaskInstance taskInstance1=  historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getId())
                .taskId(task1.getId())
                .singleResult();
        Assert.isTrue("admin0001".equals(taskInstance1.getAssignee()),"任务1负责人错误");
        // 4.1 任务2的负责人是方法赋值：UserService.getLoginUser()
        HistoricTaskInstance taskInstance2=  historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getId())
                .taskId(task2.getId())
                .singleResult();
        Assert.isTrue("login-admin-001".equals(taskInstance2.getAssignee()),"任务2负责人错误");
    }

    @Test
    void task_assignee_listener_user_add_success(){
        // 1. 开始流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_listener");
        // 2. 找到对应任务
        Task task= taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("Activity_14bkfeu")
                .singleResult();
        // 3. 完成任务
        taskService.complete(task.getId());
        // 4. 查询工作流的任务历史实例信息
        HistoricTaskInstance taskInstance=  historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("Activity_14bkfeu")
                .singleResult();
        // 5. 断言历史任务实例信息的负责人是对的
        Assert.isTrue("dynamicAssignee001".equals(taskInstance.getAssignee()),"任务负责人不对");
    }

    /**
     * 委派
     */
    @Test
    void delegateTest(){
        // 1. 开始流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_listener");
        // 2. 找到对应任务
        Task task= taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("Activity_14bkfeu")
                .singleResult();
        task.delegate("delegateUser"); // 被委派人
        task.getOwner();// 委派人
        taskService.delegateTask(task.getId(),"delegateUser");
        // 被委托人解决任务
        taskService.resolveTask(task.getId());

        taskService.complete(task.getId());
    }

    /**
     * 转办
     */
    @Test
    void changedAssignee(){
        // 1. 开始流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_listener");
        // 2. 找到对应任务
        Task task= taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("Activity_14bkfeu")
                .singleResult();
        task.setAssignee("user2");
        taskService.setAssignee(task.getId(),"user2");
        taskService.complete(task.getId());
    }
}

