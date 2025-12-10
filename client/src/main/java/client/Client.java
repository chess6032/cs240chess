package client;

import static client.Client.State.*;
import static ui.uidrawing.UIDrawer.*;

import chess.ChessGame;
import chess.ChessMove;
import model.AuthData;
import model.GameData;
import ui.*;
import ui.phases.GameplayUI;
import ui.phases.PostLoginUI;
import ui.phases.PreLoginUI;
import ui.phases.UiPhase;
import ui.uidrawing.BoardDrawer;
import websocket.commands.*;
import websocket.messages.*;

public class Client {
    private final ServerFacade server;
    private final WebsocketFacade ws;
    private final String wsURL;

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

    public Client(String serverURL, boolean useUnicode) throws Exception {
        server = new ServerFacade("http://" + serverURL);
        wsURL = "ws://" + serverURL + "/ws";
        ws = new WebsocketFacade(this);

        if (useUnicode) {
            BoardDrawer.useUniPieces();
        }

        state = PRELOGIN;
        phase = new PreLoginUI(server);
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

            // FIXME: if some state update happens before user inputs something...then what?

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

            // TODO: do websocket here maybe?
            if (state == GAMEPLAY) {
                assert phase.getClass() == GameplayUI.class;
                sendWsMessageIfNecessary();
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
            }
            else if (newState == GAMEPLAY) {
//                phase = new GameplayUI(server, gameData, teamColor);
                sendConnectCommand();
                // TODO: do all that latch stuff from echo to wait for a load game message
            }
            state = newState;
        }
    }

    public String getWsURL() {
        return wsURL;
    }

    // WEBSOCKET HANDLERS

    public void handleError(ErrorServerMessage msg) {
        assert phase.getClass() == GameplayUI.class;
        GameplayUI.printWsError(msg);
    }

    public void handleNotification(NotificationMessage msg) {
        assert phase.getClass() == GameplayUI.class;
        GameplayUI.evaluateWsNotifPrint(msg).run();
    }

    public void handleLoadGame(LoadGameMessage msg) {
        assert phase.getClass() == GameplayUI.class;

        var meta = msg.getGameMeta();
        var game = msg.getChessGame();
        var gameData = new GameData(meta.gameID(), meta.whiteUsername(), meta.blackUsername(), meta.gameName(), game);

        phase = new GameplayUI(server, gameData, teamColor);
        ((GameplayUI) phase).drawBoard();
    }

    private void sendWsMessageIfNecessary() {
        var commandType = ((GameplayUI) phase).getCommandType();
        if (commandType != null) {
            assert commandType != UserGameCommand.CommandType.CONNECT;
            switch (commandType) {
                case UserGameCommand.CommandType.MAKE_MOVE -> sendMakeMoveCommand(((GameplayUI) phase).getMove());
                case UserGameCommand.CommandType.LEAVE -> sendLeaveCommand();
                case UserGameCommand.CommandType.RESIGN -> sendResignCommand();
            }
        }
    }

    private void sendConnectCommand() {

    }

    private void sendMakeMoveCommand(ChessMove move) {

    }

    private void sendLeaveCommand() {

    }

    private void sendResignCommand() {

    }
}
