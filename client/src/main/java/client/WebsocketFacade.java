package client;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.io.IOException;
import java.net.URI;

import ui.uidrawing.UIDrawer;
import websocket.messages.ServerMessage;

public class WebsocketFacade extends Endpoint {
    public Session session;

    public WebsocketFacade(String serverURL) throws Exception {
        // open connection to websocket server
        URI uri = new URI(serverURL);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        // create handlers
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                // deserialize message
                var gson = ServerMessage.buildServerMessageGson();
                
            }
        });
    }

    @Override
    // this method must be overridden, but we don't have to actually do anything in it. lol.
    public void onOpen(Session session, EndpointConfig endpointConfig) {
//        UIDrawer.println("ws opened");
    }

    @Override
    public void onClose(Session session, jakarta.websocket.CloseReason closeReason) {
//        UIDrawer.println("ws closed: " + closeReason);
    }

    @Override
    public void onError(Session session, Throwable thr) {
//        UIDrawer.println("ws error: " + thr.getMessage());
    }

    public void send(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

}
