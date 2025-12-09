package server.handlers;

import com.google.gson.Gson;
import dataaccess.GameDAO;
import dataaccess.exceptions.SqlException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.exceptions.UnauthorizedException;

import server.Server;
import server.WsConnectionManager;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;

import static websocket.commands.UserGameCommand.buildUserGameCommandGson;
import static websocket.messages.ServerMessage.buildServerMessageGson;

public class WsRequestHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Server server;
    private final GameDAO gameDAO;
    private final WsConnectionManager connections = new WsConnectionManager();

    private final Gson userGameCommandGson = buildUserGameCommandGson();
    private final Gson serverMessageGson = buildServerMessageGson();

    public WsRequestHandler(Server server) {
        this.server = server;
        gameDAO = server.getGameDAO();
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket connected.");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        // deserialize UserGameCommand
        System.out.println("message received: " + ctx.message());

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
        if (!connections.saveSession(gameID, session)) {
            throw new RuntimeException("Failed to save session: (" + gameID + ") " + session);
        }
    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(serverMessageGson.toJson(message));
    }

    private void sendMessageToMany(Collection<Session> sessions, ServerMessage message) throws IOException {
        for (var session : sessions) {
            sendMessage(session, message);
        }
    }

    private String getUsername(String authToken) throws SqlException {
        return server.getUsernameForAuth(authToken);
    }

    private void connectUser(int gameID, Session connector, String username, ConnectCommand command) throws GameHasNoConnectionsException, IOException, SqlException {
        assert connections.sessionIsInThisGame(connector, gameID);

        GameData gameData = gameDAO.getGame(gameID);

        sendMessage(connector, new LoadMessage(gameData));

//        var loadMessage = new ServerMessage(LOAD_GAME);
//        sendMessage(connector, loadMessage);
//
//        var sessionsInThisGame = connections.sessionsInGameID(gameID);
//        var
//        sendMessage()
    }

    private void makeMove(int gameID, Session session, String username, MakeMoveCommand command) {

    }

    private void leaveGame(int gameID, Session session, String username, LeaveCommand command) {

    }

    private void resign(int gameID, Session session, String username, ResignCommand command) {

    }

    public static void main(String[] args) {

    }

    private static void testUserGameCommandDeserialization() {
        // test UserGameCommand subclass deserialization
        var specialGson = buildUserGameCommandGson();

//        var ugCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, "auth1", 67);
        var mmCommand = new MakeMoveCommand("auth2", 69, new chess.ChessMove(new chess.ChessPosition(1, 1), new chess.ChessPosition(8, 8), null));
        var cCommand = new ConnectCommand("auth3", 420);
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
