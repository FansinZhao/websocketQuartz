package com.fansin.spring.websocket.client;

/**
 * Created by zhaofeng on 16-5-12.
 * Desc:
 */
public interface IWebSocketService {
    /**
     * 用户自定义业务处理入口
     *
     * @param message
     * @throws Exception
     */
    void execute(String message) throws Exception;
}
