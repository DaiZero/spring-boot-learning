package com.dzero.wf.camunda.demo.process;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.SuspendedEntityInteractionException;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 暂停、激活 流程定义/实例
 * SuspendActivateProcessTests
 *
 * @author DaiZedong
 */
@Slf4j
@SpringBootTest
public class SuspendActivateProcessTests {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    /**
     * 通过流程ID暂停流程定义
     */
    @Test
    void suspendProcessDefinitionById() {
        // 1。找到流程定义
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey("loanApproval")
                .singleResult();
        // 2。获取流程定义ID 和 是否暂停的状态
        String pdId = processDefinition.getId();
        boolean isSuspended = processDefinition.isSuspended();
        // 3。更新流程定义和流程定义实例的流程状态
        if (!isSuspended) {
            // 该方法参数：String processDefinitionId, boolean suspendProcessInstances, Date suspensionDate
            repositoryService.suspendProcessDefinitionById(pdId, true, null);
        }
    }

    /**
     * 通过流程Key暂停流程定义
     */
    @Test
    void suspendProcessDefinitionByKey() {
        repositoryService.suspendProcessDefinitionByKey("loanApproval", true, null);
    }

    /**
     * 开始暂停的流程定义会抛出 SuspendedEntityInteractionException 异常
     */
    @Test
    void startTheSuspendedProcessInstance_ThrowSuspendedEntityInteractionException() {
        suspendProcessDefinitionByKey();
        Assertions.assertThrows(SuspendedEntityInteractionException.class, () -> {
            runtimeService.startProcessInstanceByKey("loanApproval");
        });
    }

    /**
     * 通过流程ID激活流程定义
     */
    @Test
    void activateProcessDefinitionById() {
        // 1。找到流程定义
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey("loanApproval")
                .singleResult();
        // 2。获取流程定义ID 和 是否暂停的状态
        String pdId = processDefinition.getId();
        boolean isSuspended = processDefinition.isSuspended();
        // 3。更新流程定义和流程定义实例的流程状态
        if (isSuspended) {
            // 该方法参数：String processDefinitionId, boolean suspendProcessInstances, Date suspensionDate
            repositoryService.activateProcessDefinitionById(pdId, true, null);
        }
    }

    /**
     * 通过流程Key激活流程定义
     */
    @Test
    void activateProcessDefinitionByKey() {
        repositoryService.activateProcessDefinitionByKey("loanApproval", true, null);
    }

    /**
     * 通过流程实例ID暂停流程实例成功
     */
    @Test
    String suspendProcessInstanceById_Success() {
        // 1. 找到流程实例
        ProcessInstance processInstance = getProcessInstanceByKey("loanApproval");
        // 2.1 获取流程实例ID
        String pdId = processInstance.getId();
        // 2.2 获取流程实例的是否暂停状态
        boolean isSuspended = processInstance.isSuspended();
        if (!isSuspended) {
            // 3. 根据流程实例ID暂停流程实例
            runtimeService.suspendProcessInstanceById(pdId);
        }
        // 4. 验证暂停成功
        processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(pdId)
                .singleResult();
        // 断言：暂停成功
        Assertions.assertTrue(processInstance.isSuspended());
        return processInstance.getId();
    }

    /**
     * 完成暂停的流程实例的任务会抛出 SuspendedEntityInteractionException 异常
     */
    @Test
    void completeSuspendedProcessInstanceTask_WithSuspendedEntityInteractionException() {
        // 1. 获取流程实例ID
        String piId = suspendProcessInstanceById_Success();
        // 2. 获取流程实例中的任务
        Task theTask = taskService.createTaskQuery()
                .processInstanceId(piId)
                .taskDefinitionKey("Task_0dfv74n")
                .singleResult();
        Assertions.assertThrows(SuspendedEntityInteractionException.class, () -> {
            // 3. 执行完成任务的操作
            taskService.complete(theTask.getId());
        });
    }

    /**
     * 通过流程实例ID激活流程实例成功
     */
    @Test
    void activateProcessInstanceById_Success() {
        ProcessInstance processInstance = getProcessInstanceByKey("loanApproval");
        String pdId = processInstance.getId();
        boolean isSuspended = processInstance.isSuspended();
        if (isSuspended) {
            runtimeService.activateProcessInstanceById(pdId);
        }
        // 验证激活成功
        processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(pdId)
                .singleResult();
        Assertions.assertFalse(processInstance.isSuspended());
    }

    ProcessInstance getProcessInstanceByKey(String key) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(key)
                .singleResult();
        if (processInstance == null) {
            processInstance = runtimeService.startProcessInstanceByKey(key);
        }
        return processInstance;
    }
}
