package server.handlers;

import chess.model.http.LoginRequest;
import chess.model.http.LoginResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import io.javalin.http.Context;
import server.CommonExceptions;
import server.ErrorMessage;
import service.UserService;

import static server.CommonExceptions.BadRequestResponse;

public class LoginHandler implements HTTPRequestHandler {
    private final Gson serializer = new Gson();
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    @Override
    public void handleRequest(Context ctx) {
        // get user data from request
        LoginRequest userData;
        try {
            userData = serializer.fromJson(ctx.body(), LoginRequest.class);
        } catch (JsonSyntaxException e) {
            BadRequestResponse(ctx);
            return;
        }

        // attempt to log in user
        LoginResult auth;
        try {
            auth = UserService.login(userData, userDAO, authDAO);
        } catch (LoginFailException e) {
            ctx.status(CommonExceptions.UNAUTHORIZED_STATUS); // 401
            ctx.json(serializer.toJson(new ErrorMessage("Error: username or password incorrect.")));
            return;
        } catch (AlreadyTakenException e) {
            ctx.status(CommonExceptions.BAD_REQUEST_STATUS); // TODO: ?
            ctx.json(serializer.toJson(new ErrorMessage("Error: user already signed in")));
            return;
        }

        ctx.status(CommonExceptions.SUCCESS_STATUS);
        ctx.json(serializer.toJson(auth));
    }
}
