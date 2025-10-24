package dataaccess.MemoryDAO;

import chess.model.UserData;
import dataaccess.UserDAO;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {

    private final HashSet<UserData> users = new HashSet<>();

    @Override
    public UserData getUser(String username) {
        for (UserData userData : users) {
            if (userData.username().equals(username)) {
                return userData;
            }
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) {
        users.add(userData);
    }

    @Override
    public void clearUsers() {
        users.clear();
    }
}
