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

import java.io.IOException;

public class Client {
    private final ServerFacade server;
    private WebsocketFacade ws;
    private final String wsURL;

    private static final String INTRO_MESSAGE = "♕ 240 Chess Client";

    private State state;
    private String username;
    private String authToken;
    private GameData gameData; // game ID in database
    private ChessGame.TeamColor teamColor;

    private UiPhase phase;

//    boolean readPortionInterruptedByWsMessage = false;

    private boolean pauseReplForConectAndLoad = false;
    private boolean pauseReplForMakeMoveAndLoad = false;

    public enum State {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY,
        EXIT
    }

    public Client(String serverURL, boolean useUnicode) throws Exception {
        server = new ServerFacade("http://" + serverURL);
        wsURL = "ws://" + serverURL + "/ws";
//        ws = new WebsocketFacade(this);

        if (useUnicode) {
            BoardDrawer.useUniPieces();
        }

        state = PRELOGIN;
        phase = new PreLoginUI(server);
    }

    private boolean restartRepl() {
//        return pauseReplForConectAndLoad ;//|| pauseReplForMakeMoveAndLoad;
        return false;
    }

    public void run() {
        println(INTRO_MESSAGE);
        resetFormatting();

        while (state != EXIT) {
            if (restartRepl()) { continue; }

            // run single iteration of REPL

            // READ
//            UiPhase.printPrompter(phase.getClass().toString()); // prints " >>> "
//            UiPhase.printPrompter(""); // prints " >>> "
            UiPhase.printPrompter(state.name());
            Runnable printFunc = null;
            CommandAndArgs cargs;
            try {
                cargs = phase.read();
            } catch (UnknownCommandFromUser e) {
                UiPhase.replPrint(UiPhase::printInvalidInputError);
                continue;
            }

            if (restartRepl()) { continue; }

            // EVAL
            ReplResult result = null;
            if (cargs != null) {
                ReplResultFR funcAndResult = phase.eval(cargs);
                printFunc = funcAndResult.printFunc();
                result = funcAndResult.result();
            }

            // BREAK: print here if result gave us nothing
            if (state != GAMEPLAY && result == null) {
                UiPhase.replPrint(printFunc);
                continue;
            }

            // TODO: do websocket here maybe?
            if (state == GAMEPLAY) {
                assert phase.getClass() == GameplayUI.class;
                try {
                    sendWsMessageIfNecessary();
                } catch (WsConnectionAlreadyClosedException | IOException e) {
                    if (((GameplayUI) phase).getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                        pauseReplForMakeMoveAndLoad = false;
                    }
                    throw new RuntimeException(e);
                }
                UiPhase.replPrint(printFunc);
                continue;
            }

            if (restartRepl()) { continue; }

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
            }
            state = newState;
        }
    }

    public String getWsURL() {
        return wsURL;
    }

    // SENDING USER GAME COMMANDS

    private void sendWsMessageIfNecessary() throws WsConnectionAlreadyClosedException, IOException {

//        assert phase.getClass() == GameplayUI.class;
        if (phase.getClass() != GameplayUI.class);
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
        try {
            ws = new WebsocketFacade(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate websocket facade: " + e.getMessage());
        }
        pauseReplForConectAndLoad = true;
//        println("sending CONNECT command...");
        try {
            ws.sendAndWait(new ConnectCommand(authToken, gameData.gameID()));
//            println("connect command sent");
        } catch (Exception e) {
            GameplayUI.printWsError(new ErrorServerMessage(e.getMessage()));
            pauseReplForConectAndLoad = false;
            return false;
        }
        return true;
    }

    private void sendMakeMoveCommand(ChessMove move) throws WsConnectionAlreadyClosedException, IOException {
        pauseReplForMakeMoveAndLoad = true;
        ws.send(new MakeMoveCommand(authToken, gameData.gameID(), move));
//        println("making move (" + move + ")...");
    }

    private void sendLeaveCommand() throws WsConnectionAlreadyClosedException, IOException {
        ws.send(new LeaveCommand(authToken, gameData.gameID()));
        state = POSTLOGIN;
        phase = new PostLoginUI(server, new AuthData(authToken, username));
    }

    private void sendResignCommand() throws WsConnectionAlreadyClosedException, IOException {
//        println("sending RESIGN command...");
        ws.send(new ResignCommand(authToken, gameData.gameID()));
        state = POSTLOGIN;
        phase = new PostLoginUI(server, new AuthData(authToken, username));
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
        var meta = msg.getGameMeta();
        var game = msg.getChessGame();
        gameData = new GameData(meta.gameID(), meta.whiteUsername(), meta.blackUsername(), meta.gameName(), game);

        phase = new GameplayUI(server, gameData, teamColor);
//        ((GameplayUI) phase).drawBoard();

        pauseReplForConectAndLoad = false;
        pauseReplForMakeMoveAndLoad = false;
    }
}
