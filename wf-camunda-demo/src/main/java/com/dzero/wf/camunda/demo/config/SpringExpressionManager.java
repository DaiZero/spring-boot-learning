package com.dzero.wf.camunda.demo.config;

import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.el.ReadOnlyMapELResolver;
import org.camunda.bpm.engine.impl.el.VariableContextElResolver;
import org.camunda.bpm.engine.impl.el.VariableScopeElResolver;
import org.camunda.bpm.engine.impl.javax.el.*;
import org.camunda.bpm.engine.spring.ApplicationContextElResolver;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * SpringExpressionManager
 *
 * @author DaiZedong
 * @date 2022/3/7 17:02
 */
public class SpringExpressionManager extends ExpressionManager {
    protected ApplicationContext applicationContext;

    /**
     * @param applicationContext the applicationContext to use. Ignored when 'beans' parameter is
     *                           not null.
     * @param beans              a map of custom beans to expose. If null, all beans in the
     *                           application-context will be exposed.
     */
    public SpringExpressionManager(ApplicationContext applicationContext, Map<Object, Object> beans) {
        super(beans);
        this.applicationContext = applicationContext;
    }

    @Override
    protected ELResolver createElResolver() {
        CompositeELResolver compositeElResolver = new CompositeELResolver();
        compositeElResolver.add(new VariableScopeElResolver());
        compositeElResolver.add(new VariableContextElResolver());

        if (beans != null) {
            // Only expose limited set of beans in expressions
            compositeElResolver.add(new ReadOnlyMapELResolver(beans));
        } else {
            // Expose full application-context in expressions
            compositeElResolver.add(new ApplicationContextElResolver(applicationContext));
        }

        compositeElResolver.add(new ArrayELResolver());
        compositeElResolver.add(new ListELResolver());
        compositeElResolver.add(new MapELResolver());
        compositeElResolver.add(new BeanELResolver());

        return compositeElResolver;
    }
}
