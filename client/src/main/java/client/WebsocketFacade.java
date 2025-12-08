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

public class WebsocketFacade extends Endpoint {
    public Session session;

    public WebsocketFacade(String serverURL) throws Exception {
        // open connection to websocket server
        URI uri = new URI(serverURL);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);


        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                UIDrawer.println(message);
            }
        });
    }

    @Override
    // this method must be overridden, but we don't have to actually do anything in it. lol.
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        UIDrawer.println("ws opened");
    }

    public void ping() throws IOException {
        session.getBasicRemote().sendText("ping");
    }
}
