package service;

import chess.model.AuthData;
import chess.model.UserData;
import dataaccess.AuthDAO;
import dataaccess.BadRequestException;
import dataaccess.UserDAO;
import dataaccess.UsernameAlreadyTakenException;

public interface UserService {

    static AuthData register(UserData request, UserDAO userDAO, AuthDAO authDAO)
            throws UsernameAlreadyTakenException, BadRequestException {

        if (request == null
                || request.username() == null || request.password() == null || request.email() == null
                || request.username().isBlank() || request.password().isBlank()) { // TODO: Do I need to check if email is blank?
            throw new BadRequestException("");
        }

        if (userDAO.getUser(request.username()) != null) {
            throw new UsernameAlreadyTakenException("Error: already taken.");
        }

        userDAO.createUser(new UserData(request.username(), request.password(), request.email()));
        AuthData authData = new AuthData(request.username());
        authDAO.createAuth(authData);
        return authData;
    }

    static void clearUsers(UserDAO userDAO) {
        userDAO.clearUsers();
    }
}
