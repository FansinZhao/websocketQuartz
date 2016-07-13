package com.fansin.spring.websocket.client;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * websocket client集成springframework
 * <p/>
 * Created by zhaofeng on 16-5-12.
 * Desc:
 */
public class WebSocketLocalClient extends WebSocketClient {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketLocalClient.class);

    /*用户自定义业务处理类*/
    private IWebSocketService service;

    /*工厂,用于重发*/
    private WebSocketFactoryBean factory;

    /*状态标识*/
    private boolean isClosed = false;

    /*重连次数*/
    private int current = 0;

    /**
     * spring 初始化客户端
     *
     * @param url
     * @throws URISyntaxException
     */
    public WebSocketLocalClient(String url) throws URISyntaxException {
        this(new URI(url), new Draft_17());
    }

    /**
     * spring 初始化客户端
     *
     * @param url
     * @param timeout
     * @throws URISyntaxException
     */
    public WebSocketLocalClient(String url,int timeout) throws URISyntaxException {
        this(new URI(url), new Draft_17(),null,timeout);
    }

    public WebSocketLocalClient(URI serverURI) {
        this(serverURI,new Draft_17());
    }

    public WebSocketLocalClient(URI serverURI,int timeout) {
        this(serverURI,new Draft_17(),null,timeout);
    }

    /**
     * Constructs a WebSocketLocalClient instance and sets it to the connect to the
     * specified URI. The channel does not attampt to connect automatically. You
     * must call <var>connect</var> first to initiate the socket connection.
     *
     * @param serverUri
     * @param draft
     */
    public WebSocketLocalClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WebSocketLocalClient(URI serverUri, Draft draft, Map<String, String> headers, int connecttimeout) {
        super(serverUri, draft, headers, connecttimeout);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("websocket client 三次握手协议信息：" + JSONObject.toJSONString(handshakedata));
    }

    /**
     * 收到消息后将消息发往用户业务处理类
     *
     * @param message
     */
    @Override
    public void onMessage(String message) {
        logger.info("接收到消息=[" + message + "]");
        if (StringUtils.isEmpty(message)) {
            throw new NullPointerException("websocket server 推送的消息为空！");
        }
        try {
            service.execute(message);
        } catch (Exception e) {
            logger.error("websocket 消息处理异常！", e);
        }
    }

    /**
     * 触发重连机制
     * @param code
     * @param reason
     * @param remote
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        //设置关闭状态
        this.setClosed(true);
        logger.info("websocket client 关闭！code: " + code + " reason: " + reason + " remote： " + remote);
        //是否开启quartz模式
        if(factory.isQuartzMode()){
            logger.debug("启用quartz模式,不进行自动重连!");
            return;
        }
        //重连
        while (!factory.reconnect() && ((factory.getReconnectMax() == 0) || (getCurrent() < factory.getReconnectMax()))){
            logger.info("尝试重连中......等待3s");
            try {
                Thread.sleep(factory.getReconnectInterval());
            } catch (InterruptedException e) {
                logger.error("重连异常!",e);
                Thread.currentThread().interrupt();
            }
            setCurrent(getCurrent()+1);
        }
        if( (factory.getReconnectMax() != 0) && (factory.getReconnectMax() == getCurrent())){
            logger.info("已达到重连上限!"+factory.getReconnectMax());
        }
    }

    /**
     * 尝试重连机制
     * @param ex
     */
    @Override
    public void onError(Exception ex) {
        logger.error("websocket client异常！", ex);
    }

    public IWebSocketService getService() {
        return service;
    }

    public void setService(IWebSocketService service) {
        this.service = service;
    }

    public WebSocketFactoryBean getFactory() {
        return factory;
    }

    public void setFactory(WebSocketFactoryBean factory) {
        this.factory = factory;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    /**
     * 初始化基础数据
     * @param service
     * @param factory
     */
    public void initProp(IWebSocketService service,WebSocketFactoryBean factory){
        this.service = service;
        this.factory = factory;
    }
}
