package com.fansin.spring.websocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhaofeng on 16-5-12.
 * Desc:
 */
public class MyWebSocketServer extends WebSocketServer {

    private static final Logger logger = LogManager.getLogger();

    private List<WebSocket> clientList = new ArrayList<WebSocket>();

    public MyWebSocketServer( int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }
    /**
     * Creates a WebSocketServer that will attempt to
     * listen on port <var>WebSocket.DEFAULT_PORT</var>.
     *
     */
    public MyWebSocketServer() throws UnknownHostException {
    }

    /**
     * Creates a WebSocketServer that will attempt to bind/listen on the given <var>address</var>.
     *
     * @param address
     */
    public MyWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    /**
     * @param address
     * @param decoders
     */
    public MyWebSocketServer(InetSocketAddress address, int decoders) {
        super(address, decoders);
    }

    /**
     * @param address
     * @param drafts
     */
    public MyWebSocketServer(InetSocketAddress address, List<Draft> drafts) {
        super(address, drafts);
    }

    /**
     * @param address
     * @param decodercount
     * @param drafts
     */
    public MyWebSocketServer(InetSocketAddress address, int decodercount, List<Draft> drafts) {
        super(address, decodercount, drafts);
    }

    /**
     * Creates a WebSocketServer that will attempt to bind/listen on the given <var>address</var>,
     * and comply with <tt>Draft</tt> version <var>draft</var>.
     *
     * @param address              The address (host:port) this server should listen on.
     * @param decodercount         The number of {@link WebSocketWorker}s that will be used to process the incoming network data. By default this will be <code>Runtime.getRuntime().availableProcessors()</code>
     * @param drafts               The versions of the WebSocket protocol that this server
     *                             instance should comply to. Clients that use an other protocol version will be rejected.
     * @param connectionscontainer Allows to specify a collection that will be used to store the websockets in. <br>
     *                             If you plan to often iterate through the currently connected websockets you may want to use a collection that does not require synchronization like a {@link CopyOnWriteArraySet}. In that case make sure that you overload {@link #removeConnection(WebSocket)} and {@link #addConnection(WebSocket)}.<br>
     *                             By default a {@link HashSet} will be used.
     * @see #removeConnection(WebSocket) for more control over syncronized operation
     * @see <a href="https://github.com/TooTallNate/Java-WebSocket/wiki/Drafts" > more about drafts
     */
    public MyWebSocketServer(InetSocketAddress address, int decodercount, List<Draft> drafts, Collection<WebSocket> connectionscontainer) {
        super(address, decodercount, drafts, connectionscontainer);
    }

    /**
     * Called after an opening handshake has been performed and the given websocket is ready to be written on.
     *
     * @param conn
     * @param handshake
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("检测到客户端！"+ conn.getRemoteSocketAddress());
        clientList.add(conn);
        String pushData = "https://github.com/171388204/websocketQuartz";
        conn.send(pushData);
        logger.info("发送消息成功！");
        int t = 10;
        while (t > 0){
            try {
                Thread.sleep(2000l);
                conn.send(pushData);
                logger.info("发送消息成功！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                --t;
            }
        }
    }

    /**
     * Called after the websocket connection has been closed.
     *
     * @param conn
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote
     **/
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("onClose");
        System.exit(0);
    }

    /**
     * Callback for string messages received from the remote host
     *
     * @param conn
     * @param message
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("onMessage");
        System.out.println("服务端接收到消息："+message);

    }

    /**
     * Called when errors occurs. If an error causes the websocket connection to fail {@link #onClose(WebSocket, int, String, boolean)} will be called additionally.<br>
     * This method will be called primarily because of IO or protocol errors.<br>
     * If the given exception is an RuntimeException that probably means that you encountered a bug.<br>
     *
     * @param conn
     * @param ex
     **/
    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("onError");
    }


    public static void main(String[] args) {
        int port = 8888;
        MyWebSocketServer server = new MyWebSocketServer(new InetSocketAddress(port));
        server.start();
        System.out.println("启动完成！");
    }
}
