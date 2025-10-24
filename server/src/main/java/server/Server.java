package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.javalin.*; // TODO: can't this just be import io.javalin.Javalin; ?
import io.javalin.http.Context;

import chess.model.*;
import dataaccess.*;
import dataaccess.MemoryDAO.*;
import service.*;

public class Server {

    private final Javalin javalin;
    private final Gson serializer = new Gson();

    // make each new DatabaseAccessObject ONLY ONCE.
    private final UserDAO userDataAccess = new MemoryUserDAO();
    private final AuthDAO authDataAccess = new MemoryAuthDAO();
    private final GameDAO gameDataAccess = new MemoryGameDAO();

    private record ErrorMessage(String message) {}

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    // EXCEPTION HANDLING

    private void BadRequestResponse(Context ctx) {
        ctx.status(ExceptionStatusCodes.BAD_REQUEST); // 400
        ctx.json(serializer.toJson(new ErrorMessage("Error: bad request")));
    }

    private void AlreadyTakenResponse(Context ctx) {
        ctx.status(ExceptionStatusCodes.ALREADY_TAKEN); // 403
        ctx.json(serializer.toJson(new ErrorMessage("Error: already taken")));
    }

    // HANDLERS

    public void clear(Context ctx) {
        // TODO: where to implement status code 500?

        UserService.clearUsers(userDataAccess);
        AuthService.clearAuths(authDataAccess);
        GameService.clearGames(gameDataAccess);
        ctx.status(ExceptionStatusCodes.SUCCESS);
        ctx.json(serializer.toJson(new JsonObject())); // empty JSON
    }

    public void register(Context ctx) {
        // TODO: where to implement status code 500?

        RegisterRequest request;
        try {
            request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        } catch (JsonSyntaxException e) {
            BadRequestResponse(ctx);
            return;
        }

        AuthData authData;
        try {
            authData = UserService.register(request, userDataAccess, authDataAccess);
        } catch (UsernameAlreadyTakenException e) {
            AlreadyTakenResponse(ctx);
            return;
        } catch (BadRequestException e) {
            BadRequestResponse(ctx);
            return;
        }

        ctx.status(ExceptionStatusCodes.SUCCESS);
        ctx.json(serializer.toJson(authData));
    }
}
