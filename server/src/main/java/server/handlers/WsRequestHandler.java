package server.handlers;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import model.NotificationInfo;
import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Collection;

import server.SessionSaveFailException;
import service.GameService;
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
    private final WsConnectionManager connections = new WsConnectionManager();

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
        System.out.println("MESSAGE RECEIVED: " + ctx.message());

        int gameID = -1;
        Session session = ctx.session;

        try {
            UserGameCommand command = userGameCommandGson.fromJson(ctx.message(), UserGameCommand.class);
            System.out.println("   command from message: " + command);
            gameID = command.getGameID();

            String username = getUsername(command.getAuthToken());
            if (username == null) {
                throw new UnauthorizedException("No username associated with " + command.getAuthToken());
            }

            saveSession(gameID, session);

            switch (command.getCommandType()) {
                case CONNECT -> connectUser(gameID, session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(gameID, session, username, (MakeMoveCommand) command );
                case LEAVE -> leaveGame(gameID, session, username, (LeaveCommand) command);
                case RESIGN -> resign(gameID, session, username, (ResignCommand) command);
            }
        } catch (UnauthorizedException e) {
            sendMessage(session, new ErrorServerMessage("unauthorized"));
        } catch (AnticipatedBadBehaviorException e) {
            sendMessage(session, new ErrorServerMessage(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(session, new ErrorServerMessage(e.getMessage()));
        }
    }


    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed.");
    }

    private void saveSession(int gameID, Session session) {
        try {
            connections.saveSession(gameID, session);
        } catch (SessionSaveFailException e) {
            System.out.println("Failed to save session: " + e.getMessage());
        }
    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        System.out.println("SENDING MESSAGE: " + message);
        session.getRemote().sendString(serverMessageGson.toJson(message));
    }

    private void sendMessageToMany(Collection<Session> sessions, ServerMessage message) throws IOException {
        for (var session : sessions) {
            sendMessage(session, message);
        }
    }

    private void sendMessageToManyWithExclusion(Collection<Session> sessions, Session excludedSession, ServerMessage message) throws IOException {
        for (var session : sessions) {
            if (!session.equals(excludedSession)) {
                sendMessage(session, message);
            }
        }
    }

    private String getUsername(String authToken) throws SqlException {
        return server.getUsernameForAuth(authToken);
    }

    private void connectUser(int gameID, Session sender, String username, ConnectCommand command) throws GameHasNoConnectionsException,
            IOException, SqlException
    {
        assert connections.sessionIsInThisGame(sender, gameID);

        // query db
        GameData gameData = gameDAO.getGame(gameID);

        var team = command.getTeam();
        // update db: add player to game
        if (team != null) { // null team means this is an observer
            System.out.println(" ~~~~~~~~~ BY MY POWERS OF **DEDUCTION** I HAVE CONLCUDED THAT THIS **CLIENT** IS **PLAYING**");
            gameDAO.addPlayerToGame(gameID, username, ChessGame.TeamColor.toString(team));
        }

        // send messages

        sendMessage(sender, new LoadMessage(gameData));

        var sessionsInThisGame = connections.sessionsInGameID(gameID);
        var notificationInfo = new NotificationInfo(username, team, null);
        sendMessageToManyWithExclusion(sessionsInThisGame, sender, new NotificationMessage(NotificationType.PLAYER_JOINED, notificationInfo));
    }

    private void makeMove(int gameID, Session sender, String username, MakeMoveCommand command) throws IOException, Exception {
        throw new Exception("poop your pants, nerd!");
    }

    private void leaveGame(int gameID, Session sender, String username, LeaveCommand command) throws SqlException, IOException, GameHasNoConnectionsException {
        assert connections.sessionIsInThisGame(sender, gameID);

        // update db: remove player from game
        gameDAO.removePlayerFromGame(gameID, username);

        // remove sender from connections manager
        connections.removeSession(gameID, sender);

        // send messages

        sendMessageToManyWithExclusion(connections.sessionsInGameID(gameID), sender,
                new NotificationMessage(NotificationType.PLAYER_LEFT, new NotificationInfo(username, command.getTeam(), null)));
    }

    private void resign(int gameID, Session sender, String username, ResignCommand command) throws SqlException, IOException, AnticipatedBadBehaviorException {
        assert connections.sessionIsInThisGame(sender, gameID);

        // query db
        var gameData = gameDAO.getGame(gameID);
        var team = getTeamColorOfUsername(username, gameData);
        var game = gameData.game();
        if (game == null) {
            throw new AnticipatedBadBehaviorException("game does not exist");
        }
        if (!game.isGameActive()) {
            throw new AnticipatedBadBehaviorException("game is already over");
        }
        if (team == null) {
            throw new AnticipatedBadBehaviorException("must be a player to resign");
        }

        // update db: make game inactive
        game.resign(team);
        gameDAO.setGame(gameID, game);

        // send message
        sendMessageToMany(connections.sessionsInGameID(gameID),
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

    private static void testUserGameCommandDeserialization() {
        // test UserGameCommand subclass deserialization
        var specialGson = buildUserGameCommandGson();

//        var ugCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, "auth1", 67);
        var mmCommand = new MakeMoveCommand("auth2", 69, new chess.ChessMove(new chess.ChessPosition(1, 1), new chess.ChessPosition(8, 8), null));
        var cCommand = new ConnectCommand("auth3", 420, null);
        System.out.println(mmCommand);
        System.out.println(cCommand);

        System.out.println();

//        System.out.println(specialGson.fromJson(new Gson().toJson(ugCommand), UserGameCommand.class));
        System.out.println(specialGson.fromJson(new Gson().toJson(mmCommand), UserGameCommand.class));
        System.out.println(specialGson.fromJson(new Gson().toJson(cCommand), UserGameCommand.class));
    }

    private static void testServerMessageSubclassSerialization() {
        // test ServerMessage subclass serialization
        var specialGson = buildServerMessageGson();

        ServerMessage err = new ErrorServerMessage("Tragedy");
        System.out.println(err);
        System.out.println(specialGson.toJson(err));

        System.out.println();

        ServerMessage load = new LoadMessage(new GameData(69, null, null, null, new chess.ChessGame()));
        System.out.println(load);
        System.out.println(specialGson.toJson(load));
    }
}
