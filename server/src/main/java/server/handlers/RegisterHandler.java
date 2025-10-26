package server.handlers;

import chess.model.AuthData;
import chess.model.UserData;
import dataaccess.exceptions.*;
import server.FailedDeserializationException;
import server.FailedSerializationException;
import service.UserService;

import static server.handlers.HandlerUtility.deserializeBody;
import static server.handlers.HandlerUtility.serialize;

import io.javalin.http.Context;

public class RegisterHandler {

    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public String handleRegisterRequest(Context ctx) throws FailedDeserializationException, FailedSerializationException,
            MissingAttributeException, AlreadyTakenException {

        UserData userData = deserializeBody(ctx, UserData.class); // throws FailedDeserializationException

        if (userData.username() == null || userData.password() == null || userData.email() == null ||
            userData.username().isBlank() || userData.password().isBlank() || userData.email().isBlank()) {
            throw new MissingAttributeException("RegisterHandler: username, password, or email null or not given");
        }

        AuthData authData = userService.register(userData); // throws AlreadyTakenException

        return serialize(authData); // throws FailedSerializationException
    }
}
