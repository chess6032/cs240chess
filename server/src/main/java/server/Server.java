package server;

import dataaccess.exceptions.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import dataaccess.*;
import dataaccess.memorydao.*;
import server.handlers.*;
import service.GameService;
import service.UserService;

import static server.ResponseUtility.*;

public class Server {

    private final Javalin javalin;

    // make each new DatabaseAccessObject ONLY ONCE.
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

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

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.post("/game", this::createGame);
        javalin.get("/game", this::listGames);
        javalin.put("/game", this::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    // HANDLERS

    public void clear(Context ctx) {
        userService.clear();
        gameService.clear();
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
        } catch (UserNotFoundException e) {
            buildErrorResponse(ctx, ResponseUtility.BAD_REQUEST_STATUS, "Error: username not registered");
            return;
        } catch (FailedSerializationException e) {
            failedSerializationResponse(ctx, "Error: failed to serialize AuthData");
            return;
        } catch (PasswordIncorrectException e) {
            unauthorizedResponse(ctx);
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
        }

        ResponseUtility.emptySuccessResponse(ctx);
    }

    public void createGame(Context ctx) {
        String json;

        try {
            json = new CreateGameHandler(gameService).handleCreateGameRequest(ctx);
        } catch (FailedSerializationException e) {
            failedSerializationResponse(ctx, "Failed to serialize gameID");
            return;
        } catch (AuthTokenNotFoundException e) {
            unauthorizedResponse(ctx);
            return;
        } catch (FailedDeserializationException e) {
            badRequestResponse(ctx);
            return;
        } catch (MissingAttributeException e) {
            buildErrorResponse(ctx, BAD_REQUEST_STATUS, "no game name provided");
            return;
        }

        successResponse(ctx, json);
    }

    public void listGames(Context ctx) {
        String json;

        try {
            json = new ListGamesHandler(gameService).handleListGamesRequest(ctx);
        } catch (FailedSerializationException e) {
            failedSerializationResponse(ctx, "Failed to serialize Collection<GameData>");
            return;
        } catch (AuthTokenNotFoundException e) {
            unauthorizedResponse(ctx);
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
        }

        emptySuccessResponse(ctx);
    }
}
