package com.dzero.wf.camunda.demo;

import com.dzero.wf.camunda.demo.service.CamundaService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@Slf4j
@SpringBootTest
class WfCamundaDemoApplicationTests {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private FormService formService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CamundaService camundaService;

    @Autowired
    private DecisionService dmnService;

    @Test
    void startServiceTaskDemo() {
        camundaService.startProcessInstanceByKey("ServiceTaskDemo_001");
    }
}
