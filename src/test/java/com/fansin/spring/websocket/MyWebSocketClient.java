package com.fansin.spring.websocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by zhaofeng on 16-5-11.
 * Desc:
 */
public class MyWebSocketClient extends WebSocketClient {

    private final static Logger logger = LogManager.getLogger();

    public MyWebSocketClient(URI serverURI) {
        super(serverURI);
    }

    public MyWebSocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public static void main(String[] args) throws Exception {

        MyWebSocketClient client = null;
        try {
            client = new MyWebSocketClient(new URI("wss://127.0.0.1:8888"), new Draft_17());
            boolean result = client.connectBlocking();
            if (result) {
                System.out.println("链接状态！" + client.getReadyState());
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("客户端 onOpen");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("客户端接收到消息 " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("客户端 关闭了!code:"+code+" reson:"+reason+" remote:"+remote);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("客户端 onError");
    }
}
