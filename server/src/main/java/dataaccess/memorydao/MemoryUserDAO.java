package dataaccess.memorydao;

import chess.model.UserData;
import dataaccess.UserDAO;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public int size() {
        return users.size();
    }

    @Override
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    @Override
    public boolean createUser(String username, String password, String email) {
        // check if username is taken
        if (userExists(username)) {
            return false;
        }
        // add user
        users.put(username, new UserData(username, password, email));
        return true;
    }
}
