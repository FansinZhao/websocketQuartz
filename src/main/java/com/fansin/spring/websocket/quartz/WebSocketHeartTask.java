/**
 *
 */
package com.fansin.spring.websocket.quartz;

import com.fansin.spring.websocket.client.WebSocketFactoryBean;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 2015-09-17
 *
 * @author zhaofeng
 */
public class WebSocketHeartTask extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHeartTask.class);

    private ApplicationContext applicationContext;

    /**
     * 从SchedulerFactoryBean注入的applicationContext.
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("开始 webSocket client 状态 任务.......");

        WebSocketFactoryBean factoryBean = applicationContext.getBean(WebSocketFactoryBean.class);
        try {
            factoryBean.heartCheck();
        } catch (Exception e) {
            logger.error("WebSocket client 异常!", e);
        }

        logger.debug("完成 webSocket client 状态 任务.......");
    }


}
