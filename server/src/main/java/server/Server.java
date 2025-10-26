package server;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.MissingAttributeException;
import io.javalin.*; // TODO: can't this just be import io.javalin.Javalin; ?
import io.javalin.http.Context;

import dataaccess.*;
import dataaccess.memorydao.*;
import server.handlers.RegisterHandler;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    // make each new DatabaseAccessObject ONLY ONCE.
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO);

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
        ResponseUtility.emptySuccessResponse(ctx);
    }

    public void register(Context ctx) {
        String json;

        try {
            json = new RegisterHandler(userService).register(ctx);
        } catch (FailedDeserializationException e) {
            ResponseUtility.badRequestResponse(ctx);
            return;
        } catch (FailedSerializationException e) {
            ResponseUtility.buildErrorResponse(ctx, ResponseUtility.GENERAL_STATUS,
                    "failed to serialize AuthData to JSON");
            return;
        } catch (MissingAttributeException e) {
            ResponseUtility.badRequestResponse(ctx);
            return;
        } catch (AlreadyTakenException e) {
            ResponseUtility.alreadyTakenResponse(ctx);
            return;
        }

        ResponseUtility.successResponse(ctx, json);
    }

    public void login(Context ctx) {

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
