package com.dzero.springbootrest2webserviceservice.config;

import com.dzero.springbootrest2webserviceservice.controller.TestController;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

/**
 * @author dzero
 * @date 2020/7/20 16:58
 */
@Configuration
public class WebServiceConfig {

    @Bean(name = "cxfServlet")
    public ServletRegistrationBean<CXFServlet> cxfServlet() {
        return new ServletRegistrationBean<>(new CXFServlet(), "/webservice/*");
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Autowired
    TestController testController;

    @Bean()
    public Endpoint TestWebServiceEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(),testController);
        endpoint.publish("/test");
//        endpoint.getInInterceptors().add(receiveInterceptor);
//        endpoint.getInInterceptors().add(exceptionInterceptor);
//        endpoint.getOutInterceptors().add(responseInterceptor);
        return endpoint;
    }




}
