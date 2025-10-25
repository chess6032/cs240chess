package server.handlers;

import chess.model.http.LoginRequest;
import chess.model.http.LoginResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.LoginFailException;
import io.javalin.http.Context;
import server.CommonResponses;
import server.ErrorMessage;
import service.UserService;

import static server.CommonResponses.BadRequestResponse;

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
        // TODO: where to implement status code 500????

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
            // username not found, or password incorrect
            ctx.status(CommonResponses.UNAUTHORIZED_STATUS); // 401
            ctx.json(serializer.toJson(new ErrorMessage("Error: username or password incorrect.")));
            return;
        } catch (AlreadyTakenException e) {
            // AuthData with username already exists
            ctx.status(CommonResponses.BAD_REQUEST_STATUS); // TODO: ?
            ctx.json(serializer.toJson(new ErrorMessage("Error: user already signed in")));
            return;
        }
        CommonResponses.SuccessResponse(ctx, auth);
    }
}
