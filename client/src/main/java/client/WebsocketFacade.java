package client;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.io.IOException;
import java.net.URI;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ui.uidrawing.UIDrawer;

public class WebsocketFacade extends Endpoint {
    public Session session;

    private CountDownLatch responseLatch;
    private volatile boolean isOpen = false;
    private String response = null;
    private final long TIMEOUT_SECONDS = 5;

    public WebsocketFacade(String serverURL) throws Exception {
        // open connection to websocket server
        URI uri = new URI(serverURL);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        // create handlers
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                response = message;
                releaseWaitingThreads();
            }
        });
    }

    public String getResponse() {
        return response;
    }

    private void releaseWaitingThreads() {
        // unblock responseLatch.await
        if (responseLatch != null) {
            responseLatch.countDown(); // counter: 1 -> 0
        }
    }

    @Override
    // this method must be overridden, but we don't have to actually do anything in it. lol.
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        UIDrawer.println("ws opened");
        isOpen = true;
    }

    @Override
    public void onClose(Session session, jakarta.websocket.CloseReason closeReason) {
        UIDrawer.println("ws closed: " + closeReason);
        isOpen = false;
        releaseWaitingThreads();
    }

    @Override
    public void onError(Session session, Throwable thr) {
        UIDrawer.println("ws error: " + thr.getMessage());
        isOpen = false;
        releaseWaitingThreads();
    }

    public void send(String message) throws IOException {
        if (!isOpen || !session.isOpen()) {
            throw new IOException("ws connection is closed");
        }
        session.getBasicRemote().sendText(message);
    }

    public boolean sendAndWait(String message, long timeoutSeconds) throws IOException {
        responseLatch = new CountDownLatch(1); // create gate w/ counter = 1
        send(message);

        try {
            // wait for WS response (with timeout)
            boolean receivedResponse = responseLatch.await(timeoutSeconds, TimeUnit.SECONDS);

            if (!receivedResponse) {
                // response not received before timeout
                return false;
            }

            return isOpen; // return false if connection was closed during wait.
        } catch (InterruptedException e) {
            // interrupted while waiting for response
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    public static void main(String[] args) throws Exception {
        var ws = new WebsocketFacade("ws://localhost:8080/ws");
        var scanner = new Scanner(System.in);

        System.out.println("Print message:");
        while(true) {
            try {
                boolean success = ws.sendAndWait(scanner.nextLine(), ws.TIMEOUT_SECONDS); // send and wait w/ 5 second timeout

                if (success) {
                    var response = ws.getResponse();
                    if (response != null && response.isEmpty()) {
                        break;
                    }
                    UIDrawer.println(response);
                } else {
                    UIDrawer.println("Failed to send/receive message");
                    break;
                }
            } catch (IOException e) {
                UIDrawer.println("Error: " + e.getMessage());
                break;
            }
        }

        UIDrawer.println("Exiting...");
    }

}
