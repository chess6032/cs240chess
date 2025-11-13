package service;

import chess.model.AuthData;
import chess.model.UserData;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.*;


public record UserService(UserDAO userDAO, AuthDAO authDAO) {

    public void clear() throws SqlException {
        userDAO.clear();
        authDAO.clear();
    }

    public AuthData register(UserData userData) throws AlreadyTakenException, MissingAttributeException, SqlException {
        if (userData.username() == null || userData.password() == null || userData.email() == null ||
                userData.username().isBlank() || userData.password().isBlank() || userData.email().isBlank()) {
            throw new MissingAttributeException("UserService.register: username, password, or email null or not given");
        }

        // create UserData (if username not already taken)
        if (!userDAO.createUser(userData.username(), userData.password(), userData.email())) {
            throw new AlreadyTakenException("UserService.register: username already taken: " + userData.username());
        }

        // create AuthData
        String authToken = authDAO.createAuth(userData.username());

        return new AuthData(authToken, userData.username());
    }

    public AuthData login(UserData requestUserData) throws UserNotFoundException, PasswordIncorrectException, MissingAttributeException,
            SqlException {
        // check input is valid
        if (requestUserData.username() == null || requestUserData.password() == null ||
                requestUserData.username().isBlank() || requestUserData.password().isBlank()) {
            throw new MissingAttributeException("UserService.login: Missing username or password");
        }

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

    public void logout(String authToken) throws AuthTokenNotFoundException, SqlException {
        if (!authDAO.deleteAuth(authToken)) {
            throw new AuthTokenNotFoundException("UserService.logout");
        }
    }
}
