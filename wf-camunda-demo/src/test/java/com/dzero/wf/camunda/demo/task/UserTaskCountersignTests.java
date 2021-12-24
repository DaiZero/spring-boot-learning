package com.dzero.wf.camunda.demo.task;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * 用户任务会签功能相关测试
 *
 * @author DaiZedong
 */
@Slf4j
@SpringBootTest
public class UserTaskCountersignTests {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    /**
     * 多实例并行会签任务_简单测试
     */
    @Test
    void user_task_countersign_parallel_simple_test() {
        // 0. 设置变量 assigneeList
        String[] assigneeList = {"user001", "user002", "user003"};
        Map<String, Object> mapVars = new HashMap<>();
        mapVars.put("assigneeList", Arrays.asList(assigneeList));
        // 1. 开始会签流程实例
        ProcessInstance procInstance = runtimeService.startProcessInstanceByKey("user_task_countersign_parallel_001", mapVars);
        String piId = procInstance.getId();
        // 2. 找到会签任务
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(piId)
                .taskDefinitionKey("activity_countersign_parallel_001")
                .list();
        // 【断言】任务实例总数
        Assert.isTrue(tasks.size() == 3, "会签任务数量错误");
        VariableInstance nrOfInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfInstances");
        VariableInstance nrOfActiveInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfActiveInstances");
        VariableInstance nrOfCompletedInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfCompletedInstances");
        // 【断言】实例数
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfInstances.getValue()).equals("3"), "总实例数错误");
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfActiveInstances.getValue()).equals("3"), "当前活动的实例数量错误");
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfCompletedInstances.getValue()).equals("0"), "已经完成的实例数量错误");
        // 完成任务实例1
        Task task1 = tasks.get(0);
        taskService.complete(task1.getId());

        nrOfInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfInstances");
        nrOfActiveInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfActiveInstances");
        nrOfCompletedInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfCompletedInstances");
        // 【断言】实例数
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfInstances.getValue()).equals("3"), "总实例数错误");
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfActiveInstances.getValue()).equals("2"), "当前活动的实例数量错误");
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfCompletedInstances.getValue()).equals("1"), "已经完成的实例数量错误");
        // 完成任务实例2
        Task task2 = tasks.get(1);
        taskService.complete(task2.getId());
        // 完成任务实例3
        Task task3 = tasks.get(2);
        taskService.complete(task3.getId());
    }

    /**
     * 多实例串行会签任务_简单测试
     */
    @Test
    void user_task_countersign_sequential_simple_test() {
        // 0. 设置变量 assigneeList
        String[] assigneeList = {"user001", "user002", "user003"};
        Map<String, Object> mapVars = new HashMap<>();
        mapVars.put("assigneeList", Arrays.asList(assigneeList));
        // 1. 开始会签流程实例
        ProcessInstance procInstance = runtimeService.startProcessInstanceByKey("user_task_countersign_sequential_001", mapVars);
        String piId = procInstance.getId();
        // 2. 任务1处理
        // 获取最新任务实例集合
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(piId)
                .taskDefinitionKey("activity_countersign_sequential_001")
                .list();
        // 【断言】任务实例总数
        Assert.isTrue(tasks.size() == 1, "会签任务数量错误");
        VariableInstance nrOfInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfInstances");
        VariableInstance nrOfActiveInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfActiveInstances");
        VariableInstance nrOfCompletedInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfCompletedInstances");
        // 【断言】实例数
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfInstances.getValue()).equals("3"), "总实例数错误");
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfActiveInstances.getValue()).equals("1"), "当前活动的实例数量错误");
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfCompletedInstances.getValue()).equals("0"), "已经完成的实例数量错误");
        Task task1 = tasks.get(0);
        // 【断言】任务1的负责人
        Assert.isTrue("user001".equals(task1.getAssignee()), "");
        // 完成任务1
        taskService.complete(task1.getId());

        // 3. 任务2处理
        // 获取最新任务实例集合
        tasks = taskService.createTaskQuery()
                .processInstanceId(piId)
                .taskDefinitionKey("activity_countersign_sequential_001")
                .list();
        nrOfInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfInstances");
        nrOfActiveInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfActiveInstances");
        nrOfCompletedInstances = getVarInstanceByPiIdAndVarName(piId, "nrOfCompletedInstances");
        // 【断言】实例数
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfInstances.getValue()).equals("3"), "总实例数错误");
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfActiveInstances.getValue()).equals("1"), "当前活动的实例数量错误");
        Assert.isTrue(ObjectUtils.nullSafeToString(nrOfCompletedInstances.getValue()).equals("1"), "已经完成的实例数量错误");
        Task task2 = tasks.get(0);
        // 【断言】任务2的负责人
        Assert.isTrue("user002".equals(task2.getAssignee()), "任务负责人错误");
        // 完成任务2
        taskService.complete(task2.getId());

        // 任务3处理
        // 获取最新任务实例集合
        tasks = taskService.createTaskQuery()
                .processInstanceId(piId)
                .taskDefinitionKey("activity_countersign_sequential_001")
                .list();
        // 【断言】任务实例总数
        Assert.isTrue(tasks.size() == 1, "会签任务数量错误");
        Task task3 = tasks.get(0);
        // 【断言】任务3的负责人
        Assert.isTrue("user003".equals(task3.getAssignee()), "任务负责人错误");
        // 完成任务3
        taskService.complete(task3.getId());

    }

    /**
     * 多实例并行会签任务_临时【加签】操作测试
     */
    @Test
    void user_task_countersign_parallel_addAssignee_success() {
        // 0. 设置变量 assigneeList
        String[] assigneeList = {"user001", "user002", "user003"};
        Map<String, Object> mapVars = new HashMap<>();
        mapVars.put("assigneeList", Arrays.asList(assigneeList));
        // 1. 开始会签流程实例
        ProcessInstance procInstance = runtimeService.startProcessInstanceByKey("user_task_countersign_parallel_001", mapVars);
        String piId = procInstance.getId();
        // 2. 进行【加签】操作，增加一个负责人：user004
        runtimeService.createProcessInstanceModification(piId)
                .startBeforeActivity("activity_countersign_parallel_001")
                .setVariable("assignee", "user004")
                .execute();
        // 3. 查询流程实例下该任务下负责人是user004的任务
        Task task4 = taskService.createTaskQuery()
                .processInstanceId(piId)
                .taskDefinitionKey("activity_countersign_parallel_001")
                .taskAssigneeIn("user004")
                .singleResult();
        // 4.【断言】
        Assert.isTrue(task4 != null && "user004".equals(task4.getAssignee()), "任务4的负责人user004不存在");

        // 5. 完成所有任务
        List<Task> taskList = taskService.createTaskQuery()
                .processInstanceId(piId)
                .taskDefinitionKey("activity_countersign_parallel_001")
                .list();
        for (Task task : taskList) {
            taskService.complete(task.getId());
        }
    }

    private VariableInstance getVarInstanceByPiIdAndVarName(String piId, String varName) {
        return runtimeService.createVariableInstanceQuery()
                .variableName(varName)
                .processInstanceIdIn(piId)
                .singleResult();
    }
}
