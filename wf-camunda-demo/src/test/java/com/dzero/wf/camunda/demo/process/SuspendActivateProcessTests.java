package com.dzero.wf.camunda.demo.process;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.SuspendedEntityInteractionException;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

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
                .latestVersion()
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
                .latestVersion()
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
        activateProcessDefinitionByKey();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(key)
                .singleResult();
        if (processInstance == null) {
            processInstance = runtimeService.startProcessInstanceByKey(key);
        }
        return processInstance;
    }

    @Test
    void checkBpmnModel() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:camunda=\"http://camunda.org/schema/1.0/bpmn\" id=\"sid-38422fae-e03e-43a3-bef4-bd33b32041b2\" targetNamespace=\"http://bpmn.io/bpmn\" exporter=\"bpmn-js (https://demo.bpmn.io)\" exporterVersion=\"5.1.2\">" +
                "  <process id=\"Process_1\" isExecutable=\"false\">" +
                "    <startEvent id=\"StartEvent_1y45yut\" name=\"开始\">" +
                "      <outgoing>SequenceFlow_0h21x7r</outgoing>" +
                "    </startEvent>" +
                "    <sequenceFlow id=\"SequenceFlow_0h21x7r\" sourceRef=\"StartEvent_1y45yut\" targetRef=\"Task_1hcentk\" />" +
                "    <userTask id=\"Task_1hcentk\" name=\"111\">" +
                "      <extensionElements>" +
                "        <camunda:properties>" +
                "          <camunda:property name=\"assignessGroups\" value=\"[{&#34;type&#34;:1,&#34;id&#34;:&#34;1507239526029217793&#34;}]\" />" +
                "          <camunda:property name=\"dealType\" value=\"1\" />" +
                "        </camunda:properties>" +
                "      </extensionElements>" +
                "      <incoming>SequenceFlow_0h21x7r</incoming>" +
                "      <outgoing>Flow_1ryb4ko</outgoing>" +
                "      <multiInstanceLoopCharacteristics camunda:collection=\"assigneeList_Task_1hcentk\" camunda:elementVariable=\"assignee\">" +
                "        <completionCondition xsi:type=\"tFormalExpression\">${nrOfCompletedInstances==1}</completionCondition>" +
                "      </multiInstanceLoopCharacteristics>" +
                "    </userTask>" +
                "    <endEvent id=\"Event_0ucsaxe\" name=\"结束\">" +
                "      <incoming>Flow_1ryb4ko</incoming>" +
                "    </endEvent>" +
                "    <sequenceFlow id=\"Flow_1ryb4ko\" sourceRef=\"Task_1hcentk\" targetRef=\"Event_0ucsaxe\" />" +
                "  </process>" +
                "  <bpmndi:BPMNDiagram id=\"BpmnDiagram_1\">" +
                "    <bpmndi:BPMNPlane id=\"BpmnPlane_1\" bpmnElement=\"Process_1\">" +
                "      <bpmndi:BPMNEdge id=\"SequenceFlow_0h21x7r_di\" bpmnElement=\"SequenceFlow_0h21x7r\">" +
                "        <omgdi:waypoint x=\"188\" y=\"120\" />" +
                "        <omgdi:waypoint x=\"240\" y=\"120\" />" +
                "      </bpmndi:BPMNEdge>" +
                "      <bpmndi:BPMNEdge id=\"Flow_1ryb4ko_di\" bpmnElement=\"Flow_1ryb4ko\">" +
                "        <omgdi:waypoint x=\"340\" y=\"120\" />" +
                "        <omgdi:waypoint x=\"392\" y=\"120\" />" +
                "      </bpmndi:BPMNEdge>" +
                "      <bpmndi:BPMNShape id=\"StartEvent_1y45yut_di\" bpmnElement=\"StartEvent_1y45yut\">" +
                "        <omgdc:Bounds x=\"152\" y=\"102\" width=\"36\" height=\"36\" />" +
                "        <bpmndi:BPMNLabel>" +
                "          <omgdc:Bounds x=\"160\" y=\"145\" width=\"22\" height=\"14\" />" +
                "        </bpmndi:BPMNLabel>" +
                "      </bpmndi:BPMNShape>" +
                "      <bpmndi:BPMNShape id=\"Activity_0i0dssy_di\" bpmnElement=\"Task_1hcentk\">" +
                "        <omgdc:Bounds x=\"240\" y=\"80\" width=\"100\" height=\"80\" />" +
                "      </bpmndi:BPMNShape>" +
                "      <bpmndi:BPMNShape id=\"Event_0ucsaxe_di\" bpmnElement=\"Event_0ucsaxe\">" +
                "        <omgdc:Bounds x=\"392\" y=\"102\" width=\"36\" height=\"36\" />" +
                "        <bpmndi:BPMNLabel>" +
                "          <omgdc:Bounds x=\"399\" y=\"145\" width=\"23\" height=\"14\" />" +
                "        </bpmndi:BPMNLabel>" +
                "      </bpmndi:BPMNShape>" +
                "    </bpmndi:BPMNPlane>" +
                "  </bpmndi:BPMNDiagram>" +
                "</definitions>";
        toBpmnModelInstance(xml);
    }

    public void toBpmnModelInstance(String xmlContent) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8))) {
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(inputStream);
            // 验证模型
            Bpmn.validateModel(modelInstance);
        } catch (Exception ex) {
            log.error("获取工作流设计内容出错");
        }
    }
}
