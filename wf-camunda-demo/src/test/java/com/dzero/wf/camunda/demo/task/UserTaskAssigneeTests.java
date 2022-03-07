package com.dzero.wf.camunda.demo.task;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.DelegationState;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户任务负责人相关测试
 *
 * @author DaiZedong
 */
@Slf4j
@SpringBootTest
public class UserTaskAssigneeTests {
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
        // 【断言】委派操作之前，Owner为空，Assignee为 001
        Assert.isTrue(task.getOwner() == null, "任务拥有人错误");
        Assert.isTrue(fUser.equals(task.getAssignee()), "任务负责人错误");
        // 3. 转办
        taskService.setAssignee(task.getId(), sUser);
        // 查询任务最新信息
        task = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        // 【断言】委派操作之前，Owner为空，Assignee为 002
        Assert.isTrue(task.getOwner() == null, "任务拥有人错误");
        Assert.isTrue(sUser.equals(task.getAssignee()), "任务负责人错误");
        // 4. 完成任务
        taskService.complete(task.getId());
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
        // 【断言】委派操作之前，Owner为空，Assignee为 001
        Assert.isTrue(task.getOwner() == null, "任务拥有人错误");
        Assert.isTrue(fUser.equals(task.getAssignee()), "任务负责人错误");
        // 3. 【任务委派】将任务委托给 002
        taskService.delegateTask(task.getId(), sUser);
        // 查询任务最新信息
        task = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        // 【断言】任务委托状态为【等待】，Owner为001，Assignee为 002
        Assert.isTrue(DelegationState.PENDING == task.getDelegationState(), "任务委托状态错误");
        Assert.isTrue(fUser.equals(task.getOwner()), "任务拥有人错误");
        Assert.isTrue(sUser.equals(task.getAssignee()), "任务负责人错误");
        // 4. 【解决任务】被委托人解决任务
        taskService.resolveTask(task.getId());
        // 查询任务最新信息
        task = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        // 【断言】委托状态为【已解决】，任务负责人返回到 001
        Assert.isTrue(DelegationState.RESOLVED == task.getDelegationState(), "任务委托状态错误");
        Assert.isTrue(fUser.equals(task.getAssignee()), "任务负责人错误");
        // 5. 完成任务
        taskService.complete(task.getId());
    }

    /**
     * 任务抢占：分发给多人的任务，有一个人声明实现抢占后，其他人不可再声明，否则会抛出异常
     */
    @Test
    void task_preempt_first_claim() {
        String[] users = {"user001", "user002", "user003"};
        Map<String, Object> mapVars = new HashMap<>();
        mapVars.put("users", Arrays.asList(users));
        // 1. 开始流程实例
        ProcessInstance procInstance = runtimeService.startProcessInstanceByKey("user_task_users", mapVars);
        // 2. 找到对应任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(procInstance.getId())
                .taskDefinitionKey("Activity_users_preempt")
                .singleResult();
        // 3.【断言】任务没有负责人
        Assert.isNull(task.getAssignee(), "已制定负责人");
        String taskId = task.getId();
        // 4. 给任务声明负责人
        taskService.claim(taskId, "user001");
        task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 5.【断言】任务负责人是user001
        Assert.isTrue("user001".equals(task.getAssignee()), "已制定负责人");
        // 6.【断言】再给任务声明负责人就会抛出异常
        Assertions.assertThrows(TaskAlreadyClaimedException.class, () -> {
            taskService.claim(taskId, "user002");
        });
        // 7. 完成任务
        taskService.complete(taskId);
    }


    /**
     * 动态负责人，变量赋值
     */
    @Test
    void dynamicAssignee() {
        // 1. 开始流程实例,添加处理人变量的赋值
        Map<String, Object> vars = new HashMap<>();
        vars.put("assignee", "test001");
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("user_task_assignee_dynamic", vars);
        // 2. 任务节点【Activity_0o6hudp】的【assignee】赋值：${assignee}
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("Activity_0o6hudp")
                .singleResult();
        // 3. 断言任务负责人无错
        Assert.isTrue("test001".equals(task.getAssignee()), "任务负责人不对");

        taskService.complete(task.getId());

        // 任务节点【Activity_0t7tutd】的【candidateUsers】赋值： ${assigneeList}
        Task tk2 = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("Activity_0t7tutd")
                .singleResult();
        // 任务负责人声明（抢占任务，声明后其他人不可再声明该任务）
        taskService.claim(tk2.getId(), "test002");
        Task task2 = taskService.createTaskQuery().taskId(tk2.getId()).singleResult();
        // 5.【断言】任务负责人
        Assert.isTrue("test002".equals(task2.getAssignee()), "已制定负责人");
        // 6.【断言】再给任务声明负责人就会抛出异常
        Assertions.assertThrows(TaskAlreadyClaimedException.class, () -> {
            taskService.claim(tk2.getId(), "test003");
        });

        // 完成任务2，并给 流程参数【assigneeList_Activity_0m4pxd7】赋值
        String[] assigneeList3 = {"test004", "test005"};
        Map<String, Object> mapVars3 = new HashMap<>();
        mapVars3.put("assigneeList_Activity_0m4pxd7", Arrays.asList(assigneeList3));
        taskService.complete(tk2.getId(),mapVars3);

        // 【任务3】并发多任务节点-【Activity_0t7tutd】
        // 任务节点【Activity_0t7tutd】的赋值： ${assigneeList_Activity_0m4pxd7}
        List<Task> task3List = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("Activity_0m4pxd7")
                .list();
        for (Task task3 : task3List) {
            taskService.complete(task3.getId());
        }
    }
}

