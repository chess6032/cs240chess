package dataaccess.memorydao;

import chess.model.AuthData;
import dataaccess.AuthDAO;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {

    private final HashSet<AuthData> authDatas = new HashSet<>();

    @Override
    public void clear() {
        authDatas.clear();
    }

    @Override
    public int size() {
        return authDatas.size();
    }

    @Override
    public String findAuthToken(String username) {
        for (var authData : authDatas) {
            if (authData.username().equals(username)) {
                return authData.authToken();
            }
        }
        return null;
    }

    @Override
    public String createAuth(String username) {
        // see if username already has a corresponding auth token
        String authTkn = findAuthToken(username);
        if (authTkn != null) {
            return authTkn;
        }

        // create auth token and add to database
        authTkn = AuthData.generateAuthToken();
        authDatas.add(new AuthData(username, authTkn));

        return authTkn;
    }
}
