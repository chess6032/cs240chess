package service;

import chess.model.AuthData;
import chess.model.UserData;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.PasswordIncorrectException;
import dataaccess.exceptions.UserNotFoundException;


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

    public AuthData login(UserData requestUserData) throws UserNotFoundException, PasswordIncorrectException {
        // find user in db
        UserData dbUserData = userDAO.getUser(requestUserData.username());
        if (dbUserData == null) {
            throw new UserNotFoundException("UserService.login: username doesn't exist: " + requestUserData.username());
        }

        // make sure password matches
        // TODO: do I need to make sure email matches?
        if (!requestUserData.password().equals(dbUserData.password())) {
            throw new PasswordIncorrectException("UserService.login: password incorrect: " + requestUserData.password());
        }

        // create/get and return auth token
        String authToken = authDAO.createAuth(requestUserData.username());
        return new AuthData(authToken, requestUserData.username());
    }

    public void logout(String authToken) throws AuthTokenNotFoundException {
        if (!authDAO.deleteAuth(authToken)) {
            throw new AuthTokenNotFoundException("UserService.logout");
        }
    }
}
