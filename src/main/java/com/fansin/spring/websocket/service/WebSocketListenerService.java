package com.fansin.spring.websocket.service;

import com.fansin.spring.websocket.client.IWebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaofeng on 16-5-10.
 * Desc:
 */
public class WebSocketListenerService implements IWebSocketService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketListenerService.class);

    /**
     * 用户业务处理方法
     * @param message
     * @throws Exception
     */
    @Override
    public void execute(String message) throws Exception{
        logger.info("接收到WebSocket消息="+message);
        logger.info("这里是用户的业务处理.....");
    }

}
