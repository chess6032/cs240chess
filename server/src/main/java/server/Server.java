package server;

import chess.model.*;
import service.*;
import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;
    private final Gson serializer = new Gson();
    // make a new DatabaseAccessObject ONCE.
    private final UserDAO userDAO = new userDAO(); // I have no idea how this would work but...

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
            ctx.status(403);
            ctx.json(serializer.toJson(new ErrorMessage("Error: username already taken")));
            return;
        }
        ctx.status(200);
        ctx.json(serializer.toJson(authData));
    }
}
