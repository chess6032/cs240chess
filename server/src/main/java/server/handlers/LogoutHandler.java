package server.handlers;

import chess.model.http.LogoutRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.AuthDAO;
import dataaccess.AuthTokenNotFoundException;
import io.javalin.http.Context;
import server.CommonResponses;
import service.AuthService;

public class LogoutHandler implements HTTPRequestHandler {
    private final AuthDAO authDAO;

    public LogoutHandler(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    @Override
    public void handleRequest(Context ctx) {
        // get auth token from request header
        LogoutRequest authToken = new LogoutRequest(ctx.header("Authorization"));

        // fulfill request
        try {
            AuthService.logout(authToken, authDAO);
        } catch (AuthTokenNotFoundException e) {
            CommonResponses.UnauthorizedResponse(ctx);
            return;
        }

        // json result: 200 {}
        CommonResponses.EmptySuccessResponse(ctx);
    }
}
