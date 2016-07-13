package com.fansin.spring.websocket;

import com.fansin.spring.websocket.client.WebSocketFactoryBean;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Created by zhaofeng on 16-1-20.
 */
public class SpringBeanFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private SpringBeanFactory() {
    }

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    /**
     *
     * 获取对象
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     */
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }


    /**
     * 获取类型为requiredType的对象
     * 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）
     *
     * @param name bean注册名
     * @return Object 返回requiredType类型对象
     */
    public static <T> T getBean(Class<T> type, String name) {

        if (getApplicationContext().containsBean(name)) {
            Object bean = getApplicationContext().getBean(name);
            if (type.isInstance(bean)) {
                return (T) bean;
            }
        }
        return null;
    }

    /**
     * 获取class类型为clazz的对象
     * 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）
     *
     * @return Object 返回clazz类型对象
     */
    public static <T> T getBean(final Class<T> clazz) {
        return clazz.cast(BeanFactoryUtils.beanOfTypeIncludingAncestors(
                applicationContext, clazz));
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name
     * @return boolean
     */
    public static boolean isSingleton(String name){
        return applicationContext.isSingleton(name);
    }

    /**
     * @param name
     * @return Class 注册对象的类型
     */
    public static Class getType(String name) {
        return applicationContext.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     * @param name
     * @return
     */
    public static String[] getAliases(String name) {
        return applicationContext.getAliases(name);
    }

    /**
     * 启动websocket
     */
    public static void webSocketClient(){
        WebSocketFactoryBean factoryBean = getApplicationContext().getBean("webSocket", WebSocketFactoryBean.class);
        factoryBean.connect();
    }

    /**
     * 手动启动quartz scheduler
     */
    public static void startScheduler(){
        SchedulerFactoryBean factoryBean = getApplicationContext().getBean(SchedulerFactoryBean.class);
        factoryBean.start();
    }

}
