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
            var gson = createSpecialGson();

            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            gameID = command.getGameID();
            System.out.println(command);

            String username = getUsername(command.getAuthToken());
            saveSession(gameID, session); // ?

            switch (command.getCommandType()) {
                default -> throw new UnauthorizedException("ur mom");
//                case CONNECT -> connectUser(gameID, session, username, command);
//                case MAKE_MOVE -> makeMove(gameID, session, username, (MakeMoveCommand) command );
//                case LEAVE -> leaveGame(gameID, session, username, command);
//                case RESIGN -> resign(gameID, session, username, command);
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


    private static Gson createSpecialGson() {
        return new GsonBuilder()
                .registerTypeAdapter(UserGameCommand.class, new UserGameCommand.UserGameCommandAdapter())
                .create();
    }

    public static void main(String[] args) {
        // test subclass deserialization
        var gson = createSpecialGson();

//        var ugCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, "auth1", 67);
        var mmCommand = new MakeMoveCommand("auth2", 69, new chess.ChessMove(new chess.ChessPosition(1, 1), new chess.ChessPosition(8, 8), null));
        var cCommand = new ConnectCommand("auth3", 420);
        System.out.println(mmCommand);
        System.out.println(cCommand);

        System.out.println();

//        System.out.println(gson.fromJson(new Gson().toJson(ugCommand), UserGameCommand.class));
        System.out.println(gson.fromJson(new Gson().toJson(mmCommand), UserGameCommand.class));
        System.out.println(gson.fromJson(new Gson().toJson(cCommand), UserGameCommand.class));
    }
}
