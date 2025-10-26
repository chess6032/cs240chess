package server;

import com.google.gson.Gson;
import io.javalin.*; // TODO: can't this just be import io.javalin.Javalin; ?
import io.javalin.http.Context;

import dataaccess.*;
import dataaccess.memorydao.*;

public class Server {

    private final Javalin javalin;

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

    }

    public void register(Context ctx) {

    }

    // FIXME: username already existing should give new auth token, not throw error
    // (I can't remember if it's doing that already rn but...)
    public void login(Context ctx) {

    }

    // FIXME: logout should NOT remove username from UserDAO's db
    public void logout(Context ctx) {

    }

    public void createGame(Context ctx) {

    }

    public void listGames(Context ctx) {

    }

    public void joinGame(Context ctx) {

    }
}
