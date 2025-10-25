package server;

import com.google.gson.Gson;
import io.javalin.*; // TODO: can't this just be import io.javalin.Javalin; ?
import io.javalin.http.Context;

import dataaccess.*;
import dataaccess.MemoryDAO.*;
import server.handlers.*;

public class Server {

    private final Javalin javalin;
    private final Gson serializer = new Gson();

    // make each new DatabaseAccessObject ONLY ONCE.
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    public GameDAO getGameDAO() {
        return gameDAO;
    }

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.post("/game", this::createGame);
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
        new ClearHandler(userDAO, authDAO, gameDAO).handleRequest(ctx);
    }

    public void register(Context ctx) {
        new RegisterHandler(userDAO, authDAO).handleRequest(ctx);
    }

    public void login(Context ctx) {
        new LoginHandler(userDAO, authDAO).handleRequest(ctx);
    }

    public void logout(Context ctx) {
        new LogoutHandler(userDAO, authDAO).handleRequest(ctx);
    }

    public void createGame(Context ctx) {
        new CreateGameHandler(authDAO, gameDAO).handleRequest(ctx);
    }
}
