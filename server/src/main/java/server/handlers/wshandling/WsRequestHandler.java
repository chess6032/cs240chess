package server.handlers.wshandling;

import chess.ChessGame;
import chess.InvalidMoveException;
import chess.InvalidMoveException.MoveError;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import model.NotificationInfo;
import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Collection;

import server.SessionSaveFailException;
import dataaccess.GameDAO;
import dataaccess.exceptions.*;
import model.GameData;

import server.Server;
import server.WsConnectionManager;

import websocket.commands.*;
import websocket.exceptions.UnauthorizedException;

import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.NotificationMessage.NotificationType;
import websocket.messages.ServerMessage;

import static websocket.commands.UserGameCommand.buildUserGameCommandGson;
import static websocket.messages.ServerMessage.buildServerMessageGson;

public class WsRequestHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Server server;
//    private final GameService gameService;
    private final GameDAO gameDAO;
    private final WsConnectionManager connMan = new WsConnectionManager();

    private static long numMessagesReceived = 0;

    private final Gson userGameCommandGson = buildUserGameCommandGson();
    private final Gson serverMessageGson = buildServerMessageGson();

    public WsRequestHandler(Server server) {
        this.server = server;
//        gameService = server.getGameService();
        gameDAO = server.getGameDAO();
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket connected.");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        // deserialize UserGameCommand
        var id = ++numMessagesReceived;
        System.out.println("=================== (" + id + ") MESSAGE RECEIVED " /*+ ctx.message()*/ + "===================");

        int gameID = -1;
        Session session = ctx.session;

        try {

            UserGameCommand command = userGameCommandGson.fromJson(ctx.message(), UserGameCommand.class);
            System.out.println("    - Command from message: " + command);
            gameID = command.getGameID();

            String username = getUsername(command.getAuthToken());
            System.out.println("    - Username associated with auth tkn: " + username);
            if (username == null) {
                throw new UnauthorizedException("No username associated with " + command.getAuthToken());
            }

            if (saveSession(gameID, username, session)) {
                System.out.println("    - Saving session for the first time");
            }

            if (gameDAO.getGame(gameID) == null) {
                System.out.println("    - Given gameID does not exist in db: " + gameID);
                throw new AnticipatedBadBehaviorException("game doesn't exist.");
            }

            System.out.println("Processing message " + id + "...");
            switch (command.getCommandType()) {
                case CONNECT -> connectUser(gameID, session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(gameID, session, username, (MakeMoveCommand) command );
                case LEAVE -> leaveGame(gameID, session, username, (LeaveCommand) command);
                case RESIGN -> resign(gameID, session, username, (ResignCommand) command);
            }
            System.out.println("-------------- " + id + " processed with no errors --------------");
        } catch (UnauthorizedException e) {
            sendMessage(session, new ErrorServerMessage("unauthorized."));
            System.out.println("-------------- " + id + " was unauthorized -------------- ");
        } catch (AnticipatedBadBehaviorException e) {
            sendMessage(session, new ErrorServerMessage(e.getMessage()));
            System.out.println("-------------- " + id + " was naughty --------------");
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(session, new ErrorServerMessage(e.getMessage()));
            System.out.println("-------------- " + id + " made something go terribly wrong --------------");
        }
    }


    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed.");
    }

    private boolean saveSession(int gameID, String username, Session session) {
        try {
            connMan.saveSession(gameID, new UsernameAndSession(username, session));
        } catch (SessionSaveFailException e) {
//            System.out.println("Failed to save session: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        System.out.println("SENDING MESSAGE: " + message);
        session.getRemote().sendString(serverMessageGson.toJson(message));
    }

    private void sendMessageToMany(Collection<UsernameAndSession> sessions, ServerMessage message) throws IOException {
        for (var userAndSesh : sessions) {
            sendMessage(userAndSesh.session(), message);
        }
    }

    private void sendMessageToManyWithExclusion(Collection<UsernameAndSession> usersAndSeshes, Session excludedSession,
                                                ServerMessage message) throws IOException {
        for (var userAndSesh : usersAndSeshes) {
            if (!excludedSession.equals(userAndSesh.session())) {
                sendMessage(userAndSesh.session(), message);
            }
        }
    }

    private String getUsername(String authToken) throws SqlException {
        return server.getUsernameForAuth(authToken);
    }

    private void connectUser(int gameID, Session sender, String username, ConnectCommand command) throws GameHasNoConnectionsException,
            IOException, SqlException
    {
        assert connMan.sessionIsInThisGame(new UsernameAndSession(username, sender), gameID);

        // query db
        GameData gameData = gameDAO.getGame(gameID);

        var team = getTeamColorOfUsername(username, gameData);
        // update db: add player to game
        if (team != null) { // null team means this is an observer
            System.out.println(" ~~~~~~~~~ BY MY POWERS OF **DEDUCTION** I HAVE CONLCUDED THAT THIS **CLIENT** IS **PLAYING**");
            gameDAO.addPlayerToGame(gameID, username, ChessGame.TeamColor.toString(team));
        } else {
            System.out.println(" ~~~~~~~~~ BY MY POWERS OF **DEDUCTION** I HAVE CONLCUDED THAT THIS **CLIENT** IS **OBSERVINGGGGGGG**");
        }

        // send messages

        sendMessage(sender, new LoadMessage(gameData));

        var sessionsInThisGame = connMan.getSessionsInGameID(gameID);
        var notificationInfo = new NotificationInfo(username, team, null);
        sendMessageToManyWithExclusion(sessionsInThisGame, sender, new NotificationMessage(NotificationType.PLAYER_JOINED, notificationInfo));
    }

    private void makeMove(int gameID, Session sender, String username, MakeMoveCommand command) throws AnticipatedBadBehaviorException,
            SqlException, IOException {
        // the passoff servers DO send a ChessMove when they send a makeMove ws request

        // query db
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();

        var team = getTeamColorOfUsername(username, gameData);
        if (team == null) {
            throw new AnticipatedBadBehaviorException("can't play if you're not a playah.");
        }
        if (game.getTeamTurn() != team) {
            throw new AnticipatedBadBehaviorException("it's not your turn, silly!");
        }
//
        var move = command.getMove();
        if (move == null) {
            throw new AnticipatedBadBehaviorException("this was not anticipated...");
        }
//
//        // let's make sure player is trying to move their own piece
//        var piece = game.getBoard().getPiece(move.getStartPosition());
//        if (piece == null || piece.getTeamColor() != team) {
//            throw new AnticipatedBadBehaviorException("you can't move a piece that's not yours, dawg");
//        }
//
//        game.evaluateIfGameIsOver();
//        if (!game.isGameActive()) {
//            throw new AnticipatedBadBehaviorException("this game is already over");
//        }

        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            String msg = switch(e.getMoveError()) {
                case MoveError.GAME_ALREADY_OVER -> "this game is already over.";
                case MoveError.NO_PIECE_AT_START_POS -> "there isn't a piece there.";
                case MoveError.NOT_PLAYERS_PIECE -> "you can't move a piece that's not yours, dawg.";
                case MoveError.ILLEGAL_MOVE -> "that's not a legal move, I'm afraid.";
            };
            throw new AnticipatedBadBehaviorException(msg);
//            throw new AnticipatedBadBehaviorException("that's an invalid move, I'm afraid");
        }


        // update db
        gameDAO.setGame(gameID, game);

        // send messages

        // send LOAD_GAME to players & observers
        var newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        sendMessageToMany(connMan.getSessionsInGameID(gameID), new LoadMessage(newGameData));

        // send NOTIFICATION to observers
        sendMessageToManyWithExclusion(connMan.getSessionsInGameID(gameID), sender,
                new NotificationMessage(NotificationType.PLAYER_MADE_MOVE,
                        new NotificationInfo(username, team, move)));
    }

    private void leaveGame(int gameID, Session sender, String username, LeaveCommand command) throws SqlException, IOException {
        assert connMan.sessionIsInThisGame(new UsernameAndSession(username, sender), gameID);

        var team = getTeamColorOfUsername(username, gameDAO.getGame(gameID));

        // update db: remove player from game
        gameDAO.removePlayerFromGame(gameID, username);

        // remove sender from connections manager
        connMan.removeSession(gameID, new UsernameAndSession(username, sender));

        // send messages
        sendMessageToManyWithExclusion(connMan.getSessionsInGameID(gameID), sender,
                new NotificationMessage(NotificationType.PLAYER_LEFT, new NotificationInfo(username, team, null)));
    }

    private void resign(int gameID, Session sender, String username, ResignCommand command) throws SqlException, IOException,
            AnticipatedBadBehaviorException {
        assert connMan.sessionIsInThisGame(new UsernameAndSession(username, sender), gameID);

        // query db
        var gameData = gameDAO.getGame(gameID);
        var team = getTeamColorOfUsername(username, gameData);
        var game = gameData.game();
        if (game == null) {
            throw new AnticipatedBadBehaviorException("game does not exist.");
        }

//        game.evaluateIfGameIsOver();
        if (!game.isGameActive()) {
            throw new AnticipatedBadBehaviorException("game is already over.");
        }
        if (team == null) {
            throw new AnticipatedBadBehaviorException("must be a player to resign.");
        }

        // update db: make game inactive
        game.resign(team);
        gameDAO.setGame(gameID, game);

        // send message
        sendMessageToMany(connMan.getSessionsInGameID(gameID),
                new NotificationMessage(NotificationType.PLAYER_RESIGNED, new NotificationInfo(username, team, null)));
    }

    private ChessGame.TeamColor getTeamColorOfUsername(String username, GameData gameData) {
        // FIXME: what if player has joined a game as both black AND white?
        if (gameData.whiteUsername() != null && username.equals(gameData.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else if (gameData.blackUsername() != null && username.equals(gameData.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }
}
