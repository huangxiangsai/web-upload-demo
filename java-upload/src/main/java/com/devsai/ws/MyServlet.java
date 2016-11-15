package com.devsai.ws;

/**
 * Created by huangxiangsai on 16/7/6.
 */
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@WebServlet(name = "WebSocket Servlet", urlPatterns = { "/wsexample" })
public class MyServlet extends WebSocketServlet {

    private static final Integer MAX_MESSAGE_SIZE = 10000000;

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("HTTP GET method not implemented.");
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(10000);
        factory.getPolicy().setMaxBinaryMessageSize(MAX_MESSAGE_SIZE);
        factory.getPolicy().setMaxBinaryMessageBufferSize(MAX_MESSAGE_SIZE);
        factory.getPolicy().setInputBufferSize(MAX_MESSAGE_SIZE);
        factory.getPolicy().setMaxTextMessageBufferSize(MAX_MESSAGE_SIZE);
        factory.getPolicy().setMaxTextMessageSize(MAX_MESSAGE_SIZE);
        factory.register(StockServiceWebSocket.class);
    }
}
