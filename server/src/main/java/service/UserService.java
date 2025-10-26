package service;

import chess.model.AuthData;
import chess.model.UserData;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.AlreadyTakenException;


public record UserService(UserDAO userDAO, AuthDAO authDAO) {

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }

    public AuthData register(UserData userData) throws AlreadyTakenException {
        // create UserData (if username not already taken)
        if (!userDAO.createUser(userData.username(), userData.password(), userData.email())) {
            throw new AlreadyTakenException("UserService.register: username already taken: " + userData.username());
        }

        // create AuthData
        String authToken = authDAO.createAuth(userData.username());

        return new AuthData(authToken, userData.username());
    }
}
