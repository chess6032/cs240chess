package server.handlers;

import chess.model.http.LogoutRequest;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.AuthTokenNotFoundException;
import io.javalin.http.Context;
import server.CommonResponses;
import service.UserService;

public class LogoutHandler implements HTTPRequestHandler {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LogoutHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    @Override
    public void handleRequest(Context ctx) {
        // get auth token from request header
        LogoutRequest authToken = new LogoutRequest(ctx.header("Authorization"));

        // fulfill request
        try {
            UserService.logout(authToken, userDAO, authDAO);
        } catch (AuthTokenNotFoundException e) {
            CommonResponses.UnauthorizedResponse(ctx);
            return;
        }

        // json result: 200 {}
        CommonResponses.EmptySuccessResponse(ctx);
    }
}
