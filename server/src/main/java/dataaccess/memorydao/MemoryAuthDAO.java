package dataaccess.memorydao;

import model.AuthData;
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
    public String createAuth(String username) {
        // create auth token and add to database

        String authTkn = AuthData.generateAuthToken();
        authDatas.add(new AuthData(authTkn, username));

        return authTkn;
    }

    @Override
    public String findUserOfAuth(String authToken) {
        for (var authData : authDatas) {
            if (authData.authToken().equals(authToken)) {
                return authData.username();
            }
        }
        return null;
    }

    @Override
    public boolean deleteAuth(String authToken) {
        String username = findUserOfAuth(authToken);
        if (username == null) {
            return false;
        }
        if (!authDatas.remove(new AuthData(authToken, username))) {
            throw new RuntimeException("MemoryAuthDAO.deleteAuth: username found but wasn't successfully deleted somehow?");
        }
        return true;
    }
}
