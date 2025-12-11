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
import ui.uidrawing.UIDrawer;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

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

//    boolean readPortionInterruptedByWsMessage = false;

    private boolean pauseRepl = false;

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
            if (pauseRepl) { continue; }
            if (phase == null) {
                continue;
            }

            // run single iteration of REPL

            // READ
            UiPhase.printPrompter(phase.getClass().toString()); // prints " >>> "
            Runnable printFunc = null;
            CommandAndArgs cargs;
            try {
                cargs = phase.read();
            } catch (UnknownCommandFromUser e) {
                printFunc = UiPhase::printInvalidInputError;
                continue;
            }

            if (pauseRepl) { continue; }

            // EVAL
            ReplResultFR funcAndResult = phase.eval(cargs);
            printFunc = funcAndResult.printFunc();
            ReplResult result = funcAndResult.result();

            // BREAK: print here if result gave us nothing
            if (result == null) {
                UiPhase.replPrint(printFunc);
                continue;
            }

            if (pauseRepl) { continue; }

            // PRINT
            UiPhase.replPrint(printFunc);

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
                sendConnectCommand();
//                if (!loadGameReceivedAfterConnect) {
//                    throw new RuntimeException("Server timed out I think");
//                }
//                phase = new GameplayUI(server, gameData, teamColor);
                phase = null;
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
        pauseRepl = true;

        var meta = msg.getGameMeta();
        var game = msg.getChessGame();
        gameData = new GameData(meta.gameID(), meta.whiteUsername(), meta.blackUsername(), meta.gameName(), game);

        phase = new GameplayUI(server, gameData, teamColor);
        ((GameplayUI) phase).drawBoard();

        pauseRepl = false;
    }

    // SENDING USER GAME COMMANDS

    private void sendWsMessageIfNecessary() throws WsConnectionAlreadyClosedException, IOException {

        assert phase.getClass() == GameplayUI.class;
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

    private boolean sendConnectCommand() {
//        println("sending CONNECT command...");
        try {
            ws.sendAndWait(new ConnectCommand(authToken, gameData.gameID()));
//            println("connect command sent");
        } catch (Exception e) {
            GameplayUI.printWsError(new ErrorServerMessage(e.getMessage()));
            return false;
        }
        return true;

    }

    private void sendMakeMoveCommand(ChessMove move) {
        println("sending MAKE_MOVE command (" + move + ")...");
    }

    private void sendLeaveCommand() throws WsConnectionAlreadyClosedException, IOException {
        ws.send(new LeaveCommand(authToken, gameData.gameID()));
    }

    private void sendResignCommand() {
        println("sending RESIGN command...");
    }
}
