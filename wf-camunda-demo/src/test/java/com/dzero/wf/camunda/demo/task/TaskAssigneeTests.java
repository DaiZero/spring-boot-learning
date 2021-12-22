package com.dzero.wf.camunda.demo.task;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.DelegationState;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
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

    /**
     * 用户任务负责人固定负责人
     */
    @Test
    void task_has_assignee_001() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_001");
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskAssignee("001")
                .singleResult();
        Assert.isTrue(task != null, "task assignee is not 001");
        runtimeService.deleteProcessInstance(processInstance.getId(), "for test");
    }

    /**
     * 用户任务负责人用EL表达式
     */
    @Test
    void task_assignee_el_user() {
        // 1. 参数值设置
        Map<String, Object> mapVal = new HashMap<>();
        mapVal.put("user1", "admin0001");
        // 2. 开始 流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_el", mapVal);
        // 3. 完成工作流任务
        // 3.1 完成任务1（负责人是前面的变量赋值）
        Task task1 = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        taskService.complete(task1.getId());
        // 3.2 完成任务2（负责人是赋值）
        Task task2 = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        taskService.complete(task2.getId());
        // 4. 查询工作流的任务历史信息
        // 4.1 任务1的负责人是变量赋值
        HistoricTaskInstance taskInstance1 = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getId())
                .taskId(task1.getId())
                .singleResult();
        Assert.isTrue("admin0001".equals(taskInstance1.getAssignee()), "任务1负责人错误");
        // 4.1 任务2的负责人是方法赋值：UserService.getLoginUser()
        HistoricTaskInstance taskInstance2 = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getId())
                .taskId(task2.getId())
                .singleResult();
        Assert.isTrue("login-admin-001".equals(taskInstance2.getAssignee()), "任务2负责人错误");
    }

    /**
     * 用户任务负责人用监听器赋值
     */
    @Test
    void task_assignee_listener_user_add_success() {
        // 1. 开始流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_listener");
        // 2. 找到对应任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("Activity_14bkfeu")
                .singleResult();
        // 3. 完成任务
        taskService.complete(task.getId());
        // 4. 查询工作流的任务历史实例信息
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("Activity_14bkfeu")
                .singleResult();
        // 5. 断言历史任务实例信息的负责人是对的
        Assert.isTrue("dynamicAssignee001".equals(taskInstance.getAssignee()), "任务负责人不对");
    }

    /**
     * 任务委派
     */
    @Test
    void task_delegate() {
        String fUser = "001";
        String sUser = "002";
        // 1. 开始流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_001");
        // 2. 找到对应任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("task_001")
                .singleResult();
        // 断言：委派操作之前，Owner为空，Assignee为 001
        Assert.isTrue(task.getOwner() == null, "任务拥有人错误");
        Assert.isTrue(fUser.equals(task.getAssignee()), "任务负责人错误");
        // 3. 【任务委派】将任务委托给 002
        taskService.delegateTask(task.getId(), sUser);
        // 查询任务最新信息
        task = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        // 断言：任务委托状态为【等待】，Owner为001，Assignee为 002
        Assert.isTrue(DelegationState.PENDING == task.getDelegationState(), "任务委托状态错误");
        Assert.isTrue(fUser.equals(task.getOwner()), "任务拥有人错误");
        Assert.isTrue(sUser.equals(task.getAssignee()), "任务负责人错误");
        // 4. 【解决任务】被委托人解决任务
        taskService.resolveTask(task.getId());
        // 查询任务最新信息
        task = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        // 断言：委托状态为【已解决】，任务负责人返回到 001
        Assert.isTrue(DelegationState.RESOLVED == task.getDelegationState(), "任务委托状态错误");
        Assert.isTrue(fUser.equals(task.getAssignee()), "任务负责人错误");
        // 5. 完成任务
        taskService.complete(task.getId());
    }

    /**
     * 任务转办
     */
    @Test
    void task_transfer() {
        String fUser = "001";
        String sUser = "002";
        // 1. 开始流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_001");
        // 2. 找到对应任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("task_001")
                .singleResult();
        // 断言：委派操作之前，Owner为空，Assignee为 001
        Assert.isTrue(task.getOwner() == null, "任务拥有人错误");
        Assert.isTrue(fUser.equals(task.getAssignee()), "任务负责人错误");
        // 3. 转办
        taskService.setAssignee(task.getId(), sUser);
        // 查询任务最新信息
        task = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        // 断言：委派操作之前，Owner为空，Assignee为 002
        Assert.isTrue(task.getOwner() == null, "任务拥有人错误");
        Assert.isTrue(sUser.equals(task.getAssignee()), "任务负责人错误");
        // 4. 完成任务
        taskService.complete(task.getId());
    }

    /**
     * 任务抢占
     */
    @Test
    void task_preempt_first_claim(){
        String[] users = {"user001","user002","user003"};
        Map<String,Object> variables = new HashMap<>();
        variables.put("users", Arrays.asList(users));
    }
}

