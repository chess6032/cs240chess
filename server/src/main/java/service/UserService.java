package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;


public record UserService(UserDAO userDAO, AuthDAO authDAO) {
    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }
}
