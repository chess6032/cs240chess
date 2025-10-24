package service;

import chess.model.http.LogoutRequest;
import dataaccess.AuthDAO;
import dataaccess.AuthTokenNotFoundException;

public interface AuthService {
    static void clearAuths(AuthDAO authDAO) {
        authDAO.clearAuths();
    }

    static void logout(LogoutRequest request, AuthDAO authDAO) throws AuthTokenNotFoundException {
        authDAO.assertAuthExists(request.authToken());
        authDAO.deleteAuth(request.authToken());
    }
}
