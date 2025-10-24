package dataaccess.MemoryDAO;

import chess.model.AuthData;
import dataaccess.AuthDAO;
import dataaccess.AuthTokenNotFoundException;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {
    private final HashSet<AuthData> auths = new HashSet<>();

    @Override
    public void createAuth(AuthData authData) {
        auths.add(authData);
    }

    @Override
    public void clearAuths() {
        auths.clear();
    }

    @Override
    public int size() {
        return auths.size();
    }

    @Override
    public boolean hasUser(String username) {
        for (var authData : auths) {
            if (authData.username().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void assertAuthExists(String authToken) throws AuthTokenNotFoundException {
        for (var authData : auths) {
            if (authData.authToken().equals(authToken)) {
                return;
            }
        }
        throw new AuthTokenNotFoundException("MemoryAuthDAO: auth token not found: " + authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        AuthData deathRowAuthData = null;
        for (var authData : auths) {
            if (authData.authToken().equals(authToken)) {
                deathRowAuthData = authData;
            }
        }
        if (deathRowAuthData == null) {
            return;
        }
        auths.remove(deathRowAuthData);
    }
}
