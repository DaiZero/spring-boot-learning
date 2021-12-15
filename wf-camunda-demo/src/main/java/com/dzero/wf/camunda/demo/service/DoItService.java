package com.dzero.wf.camunda.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * DoItService
 *
 * @author DaiZedong
 */
@Service
@Slf4j
public class DoItService {
    public Boolean doIt() {
        log.info("调用 DoItService 的 doIt 方法");
        return true;
    }
}
