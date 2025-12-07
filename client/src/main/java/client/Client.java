package client;

import static client.Client.State.*;
import static ui.uidrawing.UIDrawer.*;

import chess.ChessBoard;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import ui.*;
import ui.uidrawing.BoardDrawer;

import java.util.Collections;

public class Client {
    private final ServerFacade server;

    private static final String INTRO_MESSAGE = "♕ 240 Chess Client";

    private State state;
    private String username;
    private String authToken;
    private GameData gameData; // game ID in database
    private ChessGame.TeamColor teamColor;

    private UiPhase phase;

    public enum State {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY,
        EXIT
    }

    public Client(String serverURL, boolean useUnicode) {
        server = new ServerFacade(serverURL);

        if (useUnicode) {
            BoardDrawer.useUniPieces();
        }

        state = PRELOGIN;
        phase = new PreLoginUI(server);
    }

    public Client(String serverURL) {
        this(serverURL, false);
    }

    public void run() {
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
                }
            }

            // update client state
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
}
