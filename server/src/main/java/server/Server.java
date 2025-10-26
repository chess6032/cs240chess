package server;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.MissingAttributeException;
import dataaccess.exceptions.PasswordIncorrectException;
import dataaccess.exceptions.UserNotFoundException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import dataaccess.*;
import dataaccess.memorydao.*;
import server.handlers.LoginHandler;
import server.handlers.RegisterHandler;
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
    private final GameService gameService = new GameService(gameDAO);

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
            failedSerializationResponse(ctx);
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
            failedSerializationResponse(ctx);
            return;
        } catch (PasswordIncorrectException e) {
            unauthorizedResponse(ctx);
            return;
        }

        successResponse(ctx, json);
    }

    public void logout(Context ctx) {

    }

    public void createGame(Context ctx) {

    }

    public void listGames(Context ctx) {

    }

    public void joinGame(Context ctx) {

    }
}
