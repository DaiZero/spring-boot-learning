package com.dzero.wf.camunda.demo;

import com.dzero.wf.camunda.demo.service.CamundaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WfCamundaDemoApplicationTests {

    @Autowired
    private CamundaService camundaService;

    @Test
    void startServiceTaskDemo(){
        camundaService.startProcessInstanceByKey("ServiceTaskDemo_001");
    }
}
