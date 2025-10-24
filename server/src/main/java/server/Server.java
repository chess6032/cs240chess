package server;

import com.google.gson.Gson;
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
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void ExceptionToResponse(Context ctx, Exception e) {
        ctx.status(ExceptionStatusCode.getCorrespondingStatusCode(e)); // set status code associated w/ exception class
        ctx.json(serializer.toJson(new ErrorMessage(e.getMessage()))); // serialize exception's message to JSON.
    }

    // HANDLERS

    public void register(Context ctx) {
        RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        AuthData authData;
        try {
            authData = RegisterService.register(request, userDAO, authDAO);
        } catch (UsernameAlreadyTakenException e) {
            ExceptionToResponse(ctx, e);
            return;
        }
        ctx.status(200);
        ctx.json(serializer.toJson(authData));
    }
}
