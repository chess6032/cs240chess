package server.handlers;

import chess.model.http.RegisterRequest;
import chess.model.http.RegisterResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.AlreadyTakenException;
import io.javalin.http.Context;
import server.CommonExceptions;
import service.UserService;
import dataaccess.*;

import static server.CommonExceptions.AlreadyTakenResponse;
import static server.CommonExceptions.BadRequestResponse;

public class RegisterHandler implements HTTPRequestHandler {
    private final Gson serializer = new Gson();
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    @Override
    public void handleRequest(Context ctx) {
        // TODO: where to implement status code 500?

        // deserialize request
        RegisterRequest userData;
        try {
            userData = serializer.fromJson(ctx.body(), RegisterRequest.class);
        } catch (JsonSyntaxException e) {
            BadRequestResponse(ctx);
            return;
        }

        // fulfill request
        RegisterResult authData;
        try {
            authData = UserService.register(userData, userDAO, authDAO);
        } catch (AlreadyTakenException e) {
            AlreadyTakenResponse(ctx);
            return;
        } catch (BadRequestException e) {
            BadRequestResponse(ctx);
            return;
        }

        ctx.status(CommonExceptions.SUCCESS_STATUS);
        ctx.json(serializer.toJson(authData));
    }
}
