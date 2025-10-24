package service;

import dataaccess.AuthDAO;

public interface AuthService {
    static void clearAuths(AuthDAO authDAO) {
        authDAO.clearAuths();
    }
}
