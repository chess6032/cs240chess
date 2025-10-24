package dataaccess.MemoryDAO;
import chess.model.UserData;


import dataaccess.UserDAO;
import kotlin.Pair;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<String, Pair<String, String>> userDatas = new HashMap<>();

    @Override
    public UserData getUser(String username) {
        if (userDatas.containsKey(username)) {
            return new UserData(username, userDatas.get(username).component1(), userDatas.get(username).component2());
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) {
        userDatas.put(userData.username(), new Pair<String, String>(userData.password(), userData.email()));
    }
}
