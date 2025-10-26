package service;

import chess.model.AuthData;
import chess.model.UserData;
import chess.model.http.*;
import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
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
        authDAO.addAuthData(authData);
        return new RegisterResult(authData.authToken(), authData.username());
    }

    static LoginResult login(LoginRequest request, UserDAO userDAO, AuthDAO authDAO) throws LoginFailException, BadRequestException {
        // find user in database
        var user = userDAO.getUser(request.username());
        if (user == null) {
            throw new BadRequestException("UserService.login: inputted username was null");
        }

        // check that passwords match
        if (!request.password().equals(user.password())) {
            throw new LoginFailException("UserService.login: incorrect password");
        }

        // check if user already has an auth token
        String authTkn = authDAO.getAuthTkn(request.username());
        if (authTkn != null) {
            return new LoginResult(request.username(), authTkn);
        }

        // generate auth token for new user
        authTkn = authDAO.createAuth(request.username());
        return new LoginResult(request.username(), authTkn);
    }


    static void clearUsers(UserDAO userDAO) {
        userDAO.clearUsers();
    }

    static void clearAuths(AuthDAO authDAO) {
        authDAO.clearAuths();
    }


    static void logout(LogoutRequest request, UserDAO userDAO, AuthDAO authDAO) throws AuthTokenNotFoundException {
        authDAO.assertAuthTknExists(request.authToken()); // throws AuthTokenNotFoundException
        authDAO.deleteAuth(request.authToken());
    }
}
