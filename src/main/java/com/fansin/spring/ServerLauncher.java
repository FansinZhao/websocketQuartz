package com.fansin.spring;

import com.fansin.spring.websocket.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhaofeng
 */
public class ServerLauncher {
    private static final Logger logger = LoggerFactory.getLogger(ServerLauncher.class);

    private static final String APP_NAME = "websocketQuartz";

    private ServerLauncher(){}

    public static void main(String[] args) {

        final ExecutorService es = Executors.newFixedThreadPool(1);
        es.execute(new Runnable() {
            @Override
            public void run() {
                ClassPathXmlApplicationContext context = null;
                try {
                    context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");
                    //启动webSocket心跳任务
                    SpringBeanFactory.startScheduler();
                    logger.info("恭喜！ " + APP_NAME + " 启动成功！");
                } catch (Exception e) {
                    logger.error("抱歉！" + APP_NAME + "启动失败:", e);
                    if(context != null){
                        context.close();
                    }
                    System.exit(0);
                }
            }

        });

    }
}
