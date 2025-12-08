package server;

import dataaccess.exceptions.*;
import dataaccess.sqldao.SqlAuthDAO;
import dataaccess.sqldao.SqlGameDAO;
import dataaccess.sqldao.SqlUserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;

import dataaccess.*;
import dataaccess.memorydao.*;
import server.handlers.*;
import service.GameService;
import service.UserService;

import static server.ResponseUtility.*;
import static server.HttpResponseCodes.*;

public class Server {

    private final Javalin javalin;

    // make each new DatabaseAccessObject ONLY ONCE.
    private final UserDAO userDAO;

    {
        try {
            userDAO = new SqlUserDAO();
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    private final AuthDAO authDAO;

    {
        try {
            authDAO = new SqlAuthDAO();
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    private final GameDAO gameDAO;

    {
        try {
            gameDAO = new SqlGameDAO();
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(userDAO, authDAO, gameDAO);

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    public GameDAO getGameDAO() {
        return gameDAO;
    }

    public UserService getUserService() {
        return userService;
    }

    public GameService getGameService() {
        return gameService;
    }

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // HTTP endpoints
        javalin.delete("/db", this::clear)
            .post("/user", this::register)
            .post("/session", this::login)
            .delete("/session", this::logout)
            .post("/game", this::createGame)
            .get("/game", this::listGames)
            .put("/game", this::joinGame);

        // WS endpoints
        var wsHandler = new WsRequestHandler();
        javalin.ws("/ws", ws -> {
            ws.onConnect(wsHandler);
            ws.onClose(wsHandler);
            ws.onMessage(wsHandler);
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    // HANDLERS

    public void clear() {
        clear(null);
    }

    public void clear(Context ctx) {
        try {
            userService.clear();
        } catch (SqlException e) {
            sqlExceptionResponse(ctx, "Server.clear: userService.clear: ", e);
        }
        try {
            gameService.clear();
        } catch (SqlException e) {
            handleSqlException(ctx, "Server.clear: gameService.clear: ", e);
            return;
        }
        if (ctx == null) {
            return;
        }
        ResponseUtility.emptySuccessResponse(ctx);
    }

    public void register(Context ctx) {
        String json;

        try {
            json = new RegisterHandler(userService).handleRegisterRequest(ctx);
        } catch (FailedDeserializationException | MissingAttributeException e) {
            badRequestResponse(ctx);
            return;
        } catch (FailedSerializationException e) {
            failedSerializationResponse(ctx, "Error: failed to serialize AuthData");
            return;
        } catch (AlreadyTakenException e) {
            alreadyTakenResponse(ctx);
            return;
        } catch (SqlException e) {
            handleSqlException(ctx, "Server.register: RegisterHandler.handleRegisterRequest: ", e);
            return;
        }

        successResponse(ctx, json);
    }

    public void login(Context ctx) {
        String json;

        try {
            json = new LoginHandler(userService).handleLoginRequest(ctx);
        } catch (FailedDeserializationException | MissingAttributeException e) {
            badRequestResponse(ctx);
            return;
        } catch (UserNotFoundException | PasswordIncorrectException e) {
            unauthorizedResponse(ctx);
            return;
        } catch (FailedSerializationException e) {
            failedSerializationResponse(ctx, "Error: failed to serialize AuthData");
            return;
        } catch (SqlException e) {
            handleSqlException(ctx, "Server.Login: LoginHandler.handleLoginRequest: ", e);
            return;
        }

        successResponse(ctx, json);
    }

    public void logout(Context ctx) {
        String authToken = ctx.header("Authorization");
        try {
            userService.logout(authToken);
        } catch (AuthTokenNotFoundException e) {
            unauthorizedResponse(ctx);
            return;
        } catch (SqlException e) {
            handleSqlException(ctx, "Server.logout: ", e);
            return;
        }

        ResponseUtility.emptySuccessResponse(ctx);
    }

    public void createGame(Context ctx) {
        String json;

        try {
            json = new CreateGameHandler(gameService).handleCreateGameRequest(ctx);
        } catch (FailedSerializationException e) {
            failedSerializationResponse(ctx, "Error: Failed to serialize gameID");
            return;
        } catch (AuthTokenNotFoundException e) {
            unauthorizedResponse(ctx);
            return;
        } catch (FailedDeserializationException e) {
            badRequestResponse(ctx);
            return;
        } catch (MissingAttributeException e) {
            buildErrorResponse(ctx, BAD_REQUEST_STATUS, "Error: no game name provided");
            return;
        } catch (SqlException e) {
            handleSqlException(ctx, "Server.createGame: CreateGamerHandler.handleCreateGameRequest: ", e);
            return;
        }

        successResponse(ctx, json);
    }

    public void listGames(Context ctx) {
        String json;

        try {
            json = new ListGamesHandler(gameService).handleListGamesRequest(ctx);
        } catch (FailedSerializationException e) {
            failedSerializationResponse(ctx, "Error: Failed to serialize Collection<GameData>");
            return;
        } catch (AuthTokenNotFoundException e) {
            unauthorizedResponse(ctx);
            return;
        } catch (SqlException e) {
            handleSqlException(ctx, "Server.listGames: ListGamesHandler.handleListGamesRequest: ", e);
            return;
        }

        successResponse(ctx, json);
    }

    public void joinGame(Context ctx) {
        try {
            new JoinGameHandler(gameService).handleJoinGameRequest(ctx);
        } catch (AuthTokenNotFoundException e) {
            unauthorizedResponse(ctx);
            return;
        } catch (FailedDeserializationException e) {
            badRequestResponse(ctx);
            return;
        } catch (MissingAttributeException e) {
            badRequestResponse(ctx);
            return;
        } catch (AlreadyTakenException e) {
            alreadyTakenResponse(ctx);
            return;
        } catch (GameNotFoundException e) {
            // FIXME: what status to give?
            buildErrorResponse(ctx, BAD_REQUEST_STATUS, "Error: game not found");
            return;
        } catch (SqlException e) {
            handleSqlException(ctx, "Server.joinGame: JoinGameHandler.handleJoinGameRequest: ", e);
            return;
        }

        emptySuccessResponse(ctx);
    }
}
