package com.devsai.ws;

/**
 * Created by huangxiangsai on 16/7/5.
 */
import com.devsai.service.UploadService;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebSocket
public class StockServiceWebSocket {

    private Session session;
    private UploadService uploadService = new UploadService();
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    // called when the socket connection with the browser is established
    @OnWebSocketConnect
    public void handleConnect(Session session) {
        this.session = session;
        session.getPolicy().setMaxBinaryMessageBufferSize(1024*1024);
        session.getPolicy().setMaxBinaryMessageSize(1024*1024);

    }


    // called when the connection closed
    @OnWebSocketClose
    public void handleClose(int statusCode, String reason) {
        System.out.println("Connection closed with statusCode="
                + statusCode + ", reason=" + reason);
    }

    // called when a message received from the browser
    @OnWebSocketMessage
    public void handleMessage(String message) {
        if (message.equals("start")) {
            send("Stock service started!");
            executor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    StockServiceWebSocket.this.send("ddd");
                }
            }, 0, 5, TimeUnit.SECONDS);

        } else if (message.equals("stop")) {
            this.stop();

        }
    }

    @OnWebSocketMessage
    public void handleMessageByByte(ByteBuffer message) {
        uploadService.saveFile(message.array());
        this.send("success");
//        if (message.equals("start")) {
//            send("Stock service started!");
//            executor.scheduleAtFixedRate(new Runnable() {
//                public void run() {
//                    StockServiceWebSocket.this.send("ddd");
//                }
//            }, 0, 5, TimeUnit.SECONDS);
//
//        } else if (message.equals("stop")) {
//            this.stop();
//
//        }
    }

    // called in case of an error
    @OnWebSocketError
    public void handleError(Throwable error) {
        error.printStackTrace();
    }

    // sends message to browser
    private void send(String message) {
        try {
            if (session.isOpen()) {
                session.getRemote().sendString(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendByte(ByteBuffer message) {
        try {
            if (session.isOpen()) {
                session.getRemote().sendBytes(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // closes the socket
    private void stop() {
        try {
            session.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}