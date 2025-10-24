package service;

import chess.model.AuthData;
import chess.model.RegisterRequest;
import chess.model.UserData;
import dataaccess.UserDataAccess;
import dataaccess.UsernameAlreadyTakenException;

public class RegisterService {

    public static AuthData register(RegisterRequest request, UserDataAccess userDAO) throws UsernameAlreadyTakenException {
        if (userDAO.getUser(request.username()) != null) {
            throw new UsernameAlreadyTakenException("Error: Username already taken.");
        }

        userDAO.createUser(new UserData(request.username(), request.password(), request.email()));
        AuthData authData = new AuthData(request.username());
        userDAO.createAuth(authData);
        return authData;
    }
}
