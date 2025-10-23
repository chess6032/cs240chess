package service;

import chess.model.AuthData;
import chess.model.RegisterRequest;
import chess.model.UserData;

public class RegisterService {

    public static AuthData register(RegisterRequest request, UserDAO userDAO) throws UsernameAlreadyTakenException {
        if (userDAO.getUsername(request.username()) != null) {
            throw new AlreadyTakenException();
        }

        userDAO.createUser(new UserData(request.username(), request.password(), request.email()));
        AuthData authData = new AuthData(request.username());
        userDAO.createAuth(authData);
        return authData;
    }
}
