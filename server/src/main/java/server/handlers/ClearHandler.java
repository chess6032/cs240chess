package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import server.CommonResponses;
import service.AuthService;
import service.GameService;
import service.UserService;

public class ClearHandler implements HTTPRequestHandler {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleRequest(Context ctx) {
        // TODO: where to implement status code 500?

        UserService.clearUsers(userDAO);
        AuthService.clearAuths(authDAO);
        GameService.clearGames(gameDAO);
        if (ctx == null) {
            return; // for testing
        }
        CommonResponses.EmptySuccessResponse(ctx);
    }
}
