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
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.ResignCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

public class WebsocketFacade extends Endpoint {
    public Session session;

    public WebsocketFacade(String serverURL) throws Exception {
        // open connection to websocket server
        System.out.println(serverURL);
        URI uri = new URI(serverURL);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        // create handlers
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String json) {
                // deserialize message
                ServerMessage msg = ServerMessage.buildServerMessageGson().fromJson(json, ServerMessage.class);

                // process message
                switch(msg.getServerMessageType()) {
                    case ERROR -> handleError((ErrorServerMessage) msg);
                    case LOAD_GAME -> handleLoadGame((LoadGameMessage) msg);
                    case NOTIFICATION -> handleNotification((NotificationMessage) msg);
                    case null, default -> handleError(new ErrorServerMessage("uhmm... I don't know how to handle this message..."));
                }
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

    public void send(UserGameCommand command) throws IOException {
        session.getBasicRemote().sendText(UserGameCommand.buildUserGameCommandGson().toJson(command));
    }

    // SERVER MESSAGE HANDLERS

    private void handleError(ErrorServerMessage msg) {
        UIDrawer.println(msg);
    }

    private void handleLoadGame(LoadGameMessage msg) {
        UIDrawer.println(msg);
    }

    private void handleNotification(NotificationMessage msg) {
        UIDrawer.println(msg);
    }

    public static void main(String[] args) throws Exception {
        var ws = new WebsocketFacade("ws://localhost:8080/ws");
        String authTkn = "89c7c64f-e304-48f8-9a0f-b1876718800c";
        int gameID = 1567;
        ws.send(new ConnectCommand(authTkn, gameID));
        ws.send(new ResignCommand(authTkn, gameID));

        UIDrawer.printlnItalics("Press Enter to exit...");
        System.in.read();
    }
}
