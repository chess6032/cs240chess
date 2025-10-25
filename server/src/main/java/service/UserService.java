package service;

import chess.model.AuthData;
import chess.model.UserData;
import chess.model.http.LoginRequest;
import chess.model.http.LoginResult;
import chess.model.http.RegisterRequest;
import chess.model.http.RegisterResult;
import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.LoginFailException;
import server.CommonResponses;

public interface UserService {

    static RegisterResult register(RegisterRequest request, UserDAO userDAO, AuthDAO authDAO)
            throws AlreadyTakenException, BadRequestException {

        // ensure request is good
        if (request == null
                || request.username() == null || request.password() == null || request.email() == null
                || request.username().isBlank() || request.password().isBlank()) { // TODO: Do I need to check if email is blank?
            throw new BadRequestException("");
        }

        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException(CommonResponses.ALREADY_TAKEN_MSG);
        }

        // add user data and auth data to database.
        userDAO.createUser(new UserData(request.username(), request.password(), request.email()));
        AuthData authData = new AuthData(request.username());
        authDAO.createAuth(authData);
        return new RegisterResult(authData.authToken(), authData.username());
    }

    static LoginResult login(LoginRequest request, UserDAO userDAO, AuthDAO authDAO) throws LoginFailException, AlreadyTakenException {
        // find user in database
        var user = userDAO.getUser(request.username());
        if (user == null) {
            throw new LoginFailException("UserService.login: username not in database");
        }

        // check that passwords match
        if (!request.password().equals(user.password())) {
            throw new LoginFailException("UserService.login: incorrect password");
        }

        // check that user isn't already logged in
        if (authDAO.hasUser(request.username())) {
            throw new AlreadyTakenException("UserService.login: user already signed in");
        }

        // generate auth token for user
        var auth = new AuthData(request.username());
        authDAO.createAuth(auth);
        return new LoginResult(auth.authToken());
    }


    static void clearUsers(UserDAO userDAO) {
        userDAO.clearUsers();
    }
}
