package server;

import chess.model.*;
import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;
    private final Gson serializer = new Gson();
    // make a new DatabaseAccessObject ONCE.

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

    public void register(Context ctx) {
        System.out.println(ctx.header("Authorization")); // gets auth token
        System.out.println(ctx.body()); // gets body (json)
        RegisterRequest regReq = serializer.fromJson(ctx.body(), RegisterRequest.class); // imports Json fields into object. (FIELDS MUST BE THE **EXACT** SAME NAME)
        System.out.println(regReq.username());
        System.out.println(regReq.password());
        System.out.println(regReq.email());
        ctx.status(69); // sets status code in HTTP response.
        ctx.json(serializer.toJson(regReq)); // sets HTTP response
//        ctx.result("Donkey Kong");

    }
}
