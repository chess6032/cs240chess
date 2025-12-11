package client;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import websocket.commands.UserGameCommand;
import websocket.messages.*;

// ------- FOR LATCHES -------
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
// ---------------------------

public class WebsocketFacade extends Endpoint {
    public Session session;
    private final Client client;

    // ------- FOR LATCHES -------
    private CountDownLatch responseLatch;
    private volatile boolean isOpen = false;
    private final static long TIMEOUT_SECONDS = 5;
    // ---------------------------

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

                releaseWaitingThreads(); // FIXME: is this fine?

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
        isOpen = true;
    }

    @Override
    public void onClose(Session session, jakarta.websocket.CloseReason closeReason) {
//        UIDrawer.println("ws closed: " + closeReason);
        isOpen = false;
        releaseWaitingThreads();
    }

    @Override
    public void onError(Session session, Throwable thr) {
        isOpen = false;
        releaseWaitingThreads();
    }

    public void send(UserGameCommand command) throws IOException, WsConnectionAlreadyClosedException {
        if (!isOpen || !session.isOpen()) {
            throw new WsConnectionAlreadyClosedException();
        }
        session.getBasicRemote().sendText(UserGameCommand.buildUserGameCommandGson().toJson(command));
    }

    private void releaseWaitingThreads() {
        // unlock responseLatch.await
        if (responseLatch != null) {
            responseLatch.countDown(); // counter: 1 -> 0
        }
    }

    public void sendAndWait(UserGameCommand command) throws IOException, WsTimeoutException, InterruptedException,
            WsConnectionClosedWhileWaitingException, WsConnectionAlreadyClosedException {
        responseLatch = new CountDownLatch(1);
        send(command);

        try {
            // wait for WS response (with timeout)
            boolean receivedResponse = responseLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!receivedResponse) {
                // response not received before timeout
                throw new WsTimeoutException();
            }

            if (!isOpen) {
                // connection was closed during wait.
               throw new WsConnectionClosedWhileWaitingException();
            }
        } catch (InterruptedException e) {
            // interrupted while waiting for response
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    // SERVER MESSAGE HANDLERS

    private void handleErrorMessage(ErrorServerMessage msg) {
        client.handleError(msg);
    }

    private void handleLoadGameMessage(LoadGameMessage msg) {
        ui.uidrawing.UIDrawer.println("LOADING GAME...");
        client.handleLoadGame(msg);
    }

    private void handleNotificationMessage(NotificationMessage msg) {
        client.handleNotification(msg);
    }
}
