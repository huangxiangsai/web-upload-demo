package com.devsai.ws;

/**
 * Created by huangxiangsai on 16/7/1.
 */
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class EventServlet extends WebSocketServlet
{
    private static final Integer MAX_MESSAGE_SIZE = 920092544;

    @Override
    public void configure(WebSocketServletFactory factory)
    {
        factory.getPolicy().setMaxBinaryMessageSize(MAX_MESSAGE_SIZE);
        factory.getPolicy().setMaxBinaryMessageBufferSize(MAX_MESSAGE_SIZE);
        factory.getPolicy().setMaxTextMessageBufferSize(MAX_MESSAGE_SIZE);
        factory.getPolicy().setMaxTextMessageSize(MAX_MESSAGE_SIZE);
        factory.register(EventSocket.class);
    }
}