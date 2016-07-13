package com.fansin.spring.websocket.client;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.util.Properties;

import static org.java_websocket.WebSocket.READYSTATE.CLOSED;
import static org.java_websocket.WebSocket.READYSTATE.NOT_YET_CONNECTED;

/**
 * Created by zhaofeng on 16-5-31.
 */
public class WebSocketFactoryBean implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketFactoryBean.class);

    /*websocket客户端*/
    private WebSocketLocalClient client;

    /*websocket客户端*/
    private WebSocketLocalClient died_client;

    /*websocket连接地址*/
    private String url;

    /*client 对应的用户自定义业务*/
    private IWebSocketService service;

    private String PROPFILE = "prop/webSocket.properties";

    /*属性*/
    private Properties props;

    /*重连机制 默认是采用内存模式,开启后由quartz触发重连*/
    private boolean quartzMode = false;

    public WebSocketFactoryBean() {
        initParam();
    }

    /**
     * 加载属性文件
     */
    private void initParam() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPFILE);
        props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            logger.error("读取默认配置文件失败!采用默认值", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                logger.error("关闭文件流失败!", e);
            }
        }
    }

    /**
     * Invoked by a BeanFactory on destruction of a singleton.
     *
     * @throws Exception in case of shutdown errors.
     *                   Exceptions will get logged but not rethrown to allow
     *                   other beans to release their resources too.
     */
    @Override
    public void destroy() throws Exception {
        //释放资源
        client.closeBlocking();
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     *
     * @throws Exception in the event of misconfiguration (such
     *                   as failure to set an essential property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        if (client == null && url == null) {
            throw new NullPointerException("缺少webSocket客户端信息!");
        }
        //如果配置为url,则新建一个client
        if (client == null) {
            int timeout = 0;
            if (StringUtils.isNotEmpty(props.getProperty("websocket.timeout"))) {
                timeout = Integer.parseInt(props.getProperty("websocket.timeout"));
                logger.debug("获取超时设置:" + timeout);
            }
            client = new WebSocketLocalClient(url, timeout);
        }
        //如果配置了业务自定义类,则绑定客户端
        if (service != null) {
            client.setService(service);
        }
        //绑定工厂类,用于重连机制
        client.setFactory(this);
    }

    /**
     * 复制一个新的client
     *
     * @return
     */
    public WebSocketLocalClient recreate() {
        //用于销毁
        died_client = client;
        int timeout = 0;
        if (StringUtils.isNotEmpty(props.getProperty("websocket.timeout"))) {
            timeout = Integer.parseInt(props.getProperty("websocket.timeout"));
            logger.debug("获取超时设置:" + timeout);
        }
        WebSocketLocalClient client = new WebSocketLocalClient(this.client.getURI(), timeout);
        client.initProp(this.client.getService(), this);
        return client;
    }

    /**
     * 触发websocket服务启动
     */
    public boolean connect() {
        logger.info("开始连接weSocket服务器.....");
        logger.debug("websocket url:" + client.getURI());
        try {
            boolean success = client.connectBlocking();
            if (success) {
                logger.info("websocket 客户端连接服务器 成功！");
            }
            return success;
        } catch (InterruptedException e) {
            logger.error("连接weSocket服务器失败!", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 重连
     *
     * @return
     */
    public boolean reconnect() {
        //如果没有连接,直接连接
        if (NOT_YET_CONNECTED.equals(client.getReadyState())) {
            return connect();
        }

        //已关闭
        if (client.isClosed()) {
            client = recreate();
            //关闭上一个客户端
            died_client.close();
        }

        //接口检测,减少不必要线程创建开销
        URI uri = client.getURI();
        try {

            Socket socket = new Socket(uri.getHost(), uri.getPort());
            socket.close();
        } catch (IOException e) {
            logger.error("websocket Server地址异常! " + uri.getHost() + " " + uri.getPort());
            Thread.currentThread().interrupt();
            return false;
        }

        //创建线程监听
        try {
            boolean success = client.connectBlocking();
            if (success) {
                logger.info("重连成功!");
                return true;
            } else {
                logger.error("重连失败");
                return false;
            }
        } catch (InterruptedException e) {
            logger.error("webSocket连接异常!", e);
            return false;
        }
    }

    public WebSocketLocalClient getClient() {
        return client;
    }

    public void setClient(WebSocketLocalClient client) {
        this.client = client;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public IWebSocketService getService() {
        return service;
    }

    public void setService(IWebSocketService service) {
        this.service = service;
    }

    public boolean isQuartzMode() {
        return quartzMode;
    }

    public void setQuartzMode(boolean quartzMode) {
        this.quartzMode = quartzMode;
    }

    /**
     * 客户端心跳检测
     */
    public void heartCheck() {

        if (NOT_YET_CONNECTED.equals(client.getReadyState()) || CLOSED.equals(client.getReadyState())) {
            boolean result = reconnect();
            //失败不做重连,等待下次心跳连接
            logger.info("连接结果:" + result);
        } else {
            logger.info("心跳检测:webSocket client客户端状态正常! " + client.getReadyState().toString());
        }
    }

    /**
     * 获取重连间隔
     *
     * @return
     */
    protected long getReconnectInterval() {
        //默认值
        long interval = 3000l;
        if (StringUtils.isNotEmpty(props.getProperty("websocket.reconnect.interval"))) {
            interval = Long.parseLong(props.getProperty("websocket.reconnect.interval"));
        }
        return interval;
    }

    /**
     * 获取重连次数
     *
     * @return
     */
    protected int getReconnectMax() {
        int max = 0;
        if (StringUtils.isNotEmpty(props.getProperty("websocket.reconnect.max"))) {
            max = Integer.parseInt(props.getProperty("websocket.reconnect.max"));
        }
        return max;
    }
}
