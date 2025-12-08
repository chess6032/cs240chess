package client;

import static client.Client.State.*;
import static ui.uidrawing.UIDrawer.*;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import ui.*;
import ui.phases.GameplayUI;
import ui.phases.PostLoginUI;
import ui.phases.PreLoginUI;
import ui.phases.UiPhase;
import ui.uidrawing.BoardDrawer;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

public class Client extends Endpoint {
    private final ServerFacade server;

    private static final String INTRO_MESSAGE = "♕ 240 Chess Client";

    private State state;
    private String username;
    private String authToken;
    private GameData gameData; // game ID in database
    private ChessGame.TeamColor teamColor;

    private UiPhase phase;

    public Session session;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        println("ws connection opened");
    }

    public enum State {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY,
        EXIT
    }

    public Client(String serverURL, boolean useUnicode) throws Exception {
        // pre-phase 6 stuff

        server = new ServerFacade(serverURL);

        if (useUnicode) {
            BoardDrawer.useUniPieces();
        }

        state = PRELOGIN;
        phase = new PreLoginUI(server);

        // websocket stuff

        URI uri = new URI("ws://localhost:8080/ws"); // TODO: give it the actual port
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                println(message);
            }
        });
    }

    public void send(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    public void run() throws IOException {
        println(INTRO_MESSAGE);
        resetFormatting();

        while (state != EXIT) {
            // run single iteration of REPL
            ReplResult result = phase.readEvalPrint();
            if (result == null) {
                continue;
            }

            var newState = result.state();

            // update client info
            if (newState != EXIT) {
                if (state == PRELOGIN) {
                    username = result.user().username();
                    authToken = result.auth().authToken();
                } else if (state == POSTLOGIN) {
                    gameData = result.gameData();
                    teamColor = result.color();
                } else if (state == GAMEPLAY) {
                    gameplayWebsockets(result);
                }
            }

            // update client state/phase
            if (newState == PRELOGIN) {
                if (!phase.getClass().equals(PreLoginUI.class)) {
                    phase = new PreLoginUI(server);
                }
            } else if (newState == POSTLOGIN) {
                if (!phase.getClass().equals(PostLoginUI.class)) {
                    phase = new PostLoginUI(server, new AuthData(authToken, username));
                }
            } else if (newState == GAMEPLAY) {
                phase = new GameplayUI(server, gameData, teamColor);
            }
            state = newState;
        }
    }

    private void gameplayWebsockets(ReplResult result) throws IOException {
        assert phase.getClass() == GameplayUI.class;

        if (result.ping()) {
            send("ping");
        }
    }
}
