package server.handlers;

import static server.handlers.HandlerUtility.deserializeBody;
import static server.handlers.HandlerUtility.serialize;

import chess.model.AuthData;
import chess.model.UserData;
import dataaccess.exceptions.MissingAttributeException;
import dataaccess.exceptions.PasswordIncorrectException;
import dataaccess.exceptions.UserNotFoundException;
import server.FailedDeserializationException;
import server.FailedSerializationException;
import service.UserService;

import io.javalin.http.Context;

public class LoginHandler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }
    
    public String handleLoginRequest(Context ctx) throws FailedDeserializationException, FailedSerializationException,
            MissingAttributeException, UserNotFoundException, PasswordIncorrectException {
        UserData userData = deserializeBody(ctx, UserData.class);

        // check input is valid
        if (userData.username() == null || userData.password() == null ||
            userData.username().isBlank() || userData.password().isBlank()) {
            throw new MissingAttributeException("LoginHandler: Missing username or password");
        }

        // log in user
        AuthData authData = userService.login(userData);

        return serialize(authData);
    }
}
