package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import dataaccess.exceptions.SqlException;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?
import org.jetbrains.annotations.NotNull;
import server.Server;
import websocket.commands.*;
import websocket.exceptions.UnauthorizedException;

import java.io.IOException;

public class WsRequestHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Server server;

    public WsRequestHandler(Server server) {
        this.server = server;
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
//            UserGameCommand command = new Gson().fromJson(
//                    ctx.message(),
//                    UserGameCommand.class
//            );

            // I got this lovely piece of code from a good friend of mine whose name starts with "C" and ends in "laude".
            // It creates a special Gson object using RuntimeTypeAdapterFactory gson-extras to allow for deserializing
            // into different classes from the same input.
            RuntimeTypeAdapterFactory<UserGameCommand> adapter =
                    RuntimeTypeAdapterFactory.of(UserGameCommand.class, "commandType")
                            .registerSubtype(ConnectCommand.class, "CONNECT")
                            .registerSubtype(MakeMoveCommand.class, "MAKE_MOVE")
                            .registerSubtype(LeaveCommand.class, "LEAVE")
                            .registerSubtype(ResignCommand.class, "RESIGN");

            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(adapter)
                    .create();

            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);

            String username = getUsername(command.getAuthToken());
            saveSession(gameID, session); // ?

            switch (command.getCommandType()) {
                case CONNECT -> connectUser(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command );
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch (UnauthorizedException e) {
            sendMessage(session, gameID, "Error: unauthorized");
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(session, gameID, "Error: " + e.getMessage());
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed.");
    }

    private void sendMessage(Session session, int gameID, String message) throws IOException {
        session.getRemote().sendString(message);
    }

    private void saveSession(int gameID, Session session) {
        // FIXME: ?

    }

    private String getUsername(String authToken) throws SqlException {
        return server.getUsernameForAuth(authToken);
    }

    private void connectUser(Session session, String username, UserGameCommand command) throws UnauthorizedException {

    }
    private void makeMove(Session session, String username, UserGameCommand command) throws UnauthorizedException {

    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws UnauthorizedException {

    }

    private void resign(Session session, String username, UserGameCommand command) throws UnauthorizedException {

    }

    public static void main(String[] args) {
        // test subclass deserialization
        RuntimeTypeAdapterFactory<UserGameCommand> adapter =
                RuntimeTypeAdapterFactory.of(UserGameCommand.class, "commandType")
                        .registerSubtype(ConnectCommand.class, "CONNECT")
                        .registerSubtype(MakeMoveCommand.class, "MAKE_MOVE")
                        .registerSubtype(LeaveCommand.class, "LEAVE")
                        .registerSubtype(ResignCommand.class, "RESIGN");

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(adapter)
                .create();

        var ugCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, "auth1", 67);
        var mmCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, "auth2", 69, new chess.ChessMove(new chess.ChessPosition(1, 1), new chess.ChessPosition(8, 8), null));
        var cCommand = new ConnectCommand(UserGameCommand.CommandType.CONNECT, "auth3", 420);


        System.out.println(gson.fromJson(new Gson().toJson(ugCommand), UserGameCommand.class));
        System.out.println(gson.fromJson(new Gson().toJson(mmCommand), UserGameCommand.class));
        System.out.println(gson.fromJson(new Gson().toJson(cCommand), UserGameCommand.class));
    }
}
