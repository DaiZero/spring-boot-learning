package com.dzero.wf.camunda.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CamundaService
 *
 * @author DaiZedong
 */
@Service
@Slf4j
public class CamundaService {
    @Autowired
    private FormService formService;

    @Autowired
    private DecisionService dmnService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;


    public List<ProcessInstance> getRunningProcessInstanceList(){
        try {
            List<ProcessInstance> runningProcessList = runtimeService.createProcessInstanceQuery().active().list();
            log.info("Running process list count is {}", runningProcessList.size());
            return runningProcessList;
        }catch (Exception e){
            log.error("Failed to get running process instance list.", e);
            return null;
        }
    }

    public ProcessInstance startProcessInstanceByKey(String key){
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);
            log.info("New process instance initiated. Process instance id is {}", processInstance.getId());
            return processInstance;
        }catch (Exception e){
            log.error("Failed to start process instance.", e);
            return null;
        }
    }

    public Map<String, Object> getTaskExProperties(String processDefinitionId, String taskKey) {
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        Activity task = (Activity) bpmnModelInstance.getModelElementById(taskKey);
        if (task != null) {
            ExtensionElements elements = task.getExtensionElements();
            if (elements == null) {
                return null;
            }
            List<CamundaProperties> camundaPropertiesList = elements.getElementsQuery()
                    .filterByType(CamundaProperties.class)
                    .list();
            if (camundaPropertiesList == null || camundaPropertiesList.isEmpty()) {
                return null;
            }
            Collection<CamundaProperty> properties = camundaPropertiesList.get(0)
                    .getCamundaProperties();
            if (properties != null && !properties.isEmpty()) {
                Map<String, Object> objectMap = new HashMap<>(2);
                for (CamundaProperty property : properties) {
                    objectMap.put(property.getCamundaName(), property.getCamundaValue());
                }
                return objectMap;
            }
        }
        return null;
    }

    public Map<String, Object> getProcessExProperties(String processDefinitionId) {
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        Collection<Process> processes = bpmnModelInstance.getModelElementsByType(Process.class);
        Process process = processes.parallelStream().findFirst().orElse(null);
        if (process != null) {
            ExtensionElements elements = process.getExtensionElements();
            if (elements == null) {
                return null;
            }
            List<CamundaProperties> camundaPropertiesList = elements.getElementsQuery().filterByType(CamundaProperties.class).list();
            if (camundaPropertiesList == null || camundaPropertiesList.isEmpty()) {
                return null;
            }
            Collection<CamundaProperty> properties = camundaPropertiesList.get(0).getCamundaProperties();
            if (properties != null && !properties.isEmpty()) {
                Map<String, Object> objectMap = new HashMap<>();
                for (CamundaProperty property : properties) {
                    objectMap.put(property.getCamundaName(), property.getCamundaValue());
                }
                return objectMap;
            }
        }
        return null;
    }
}
