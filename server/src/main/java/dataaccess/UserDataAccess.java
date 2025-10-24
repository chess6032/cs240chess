package dataaccess;
import chess.model.AuthData;
import chess.model.UserData;


import kotlin.Pair;

import java.util.HashMap;
import java.util.HashSet;

public class UserDataAccess {

    private final HashMap<String, Pair<String, String>> userDatas = new HashMap<>();
    private final HashSet<AuthData> authTokens = new HashSet<>();

    public UserData getUser(String username) {
        // FIXME: TEMPORARY
        if (userDatas.containsKey(username)) {
            return new UserData(username, userDatas.get(username).component1(), userDatas.get(username).component2());
        }
        return null;
    }

    public void createUser(UserData userData) {
        // FIXME: TEMPORARY
        userDatas.put(userData.username(), new Pair<String, String>(userData.password(), userData.email()));
    }

    public void createAuth(AuthData authData) {
        // FIXME: STUB
    }
}
