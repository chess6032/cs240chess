package client;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import websocket.commands.UserGameCommand;
import websocket.messages.*;

public class WebsocketFacade extends Endpoint {
    public Session session;
    private final Client client;

    public WebsocketFacade(Client client) throws Exception {
        this.client = client;
        connectToWs(client.getWsURL());
    }

    private void connectToWs(String serverURL) throws URISyntaxException, DeploymentException, IOException {
        // open connection to websocket server
//        System.out.println(serverURL);
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
                    case ERROR -> handleErrorMessage((ErrorServerMessage) msg);
                    case LOAD_GAME -> handleLoadGameMessage((LoadGameMessage) msg);
                    case NOTIFICATION -> handleNotificationMessage((NotificationMessage) msg);
                    case null, default -> handleErrorMessage(new ErrorServerMessage("uhmm... I don't know how to handle this message..."));
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

    private void handleErrorMessage(ErrorServerMessage msg) {
        client.handleError(msg);
    }

    private void handleLoadGameMessage(LoadGameMessage msg) {
        client.handleLoadGame(msg);
    }

    private void handleNotificationMessage(NotificationMessage msg) {
        client.handleNotification(msg);
    }

    // main method for quick testing

//    public static void main(String[] args) throws Exception {
//        var ws = new WebsocketFacade("ws://localhost:8080/ws");
//        String authTkn = "89c7c64f-e304-48f8-9a0f-b1876718800c";
//        int gameID = 1567;
//        ws.send(new ConnectCommand(authTkn, gameID));
//        ws.send(new ResignCommand(authTkn, gameID));
//
//        UIDrawer.printlnItalics("Press Enter to exit...");
//        System.in.read();
//    }

    // USER GAME COMMAND SENDERS
}
