package server;

import chess.model.*;
import dataaccess.UserDAO;
import dataaccess.UsernameAlreadyTakenException;
import service.*;
import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;
    private final Gson serializer = new Gson();
    // make each new DatabaseAccessObject ONLY ONCE.
    private final UserDAO userDAO = new UserDAO();

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

    // HANDLERS

    public void register(Context ctx) {
        RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        AuthData authData;
        try {
            authData = RegisterService.register(request, userDAO);
        } catch (UsernameAlreadyTakenException e) {
            ctx.status(UsernameAlreadyTakenException.httpStatus);
            ctx.json(serializer.toJson(e.getErrorMessage()));
            return;
        }
        ctx.status(200);
        ctx.json(serializer.toJson(authData));
    }
}
