package dataaccess.MemoryDAO;

import chess.model.AuthData;
import dataaccess.AuthDAO;
import dataaccess.exceptions.AuthTokenNotFoundException;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {
    private final HashSet<AuthData> auths = new HashSet<>();

    @Override
    public void addAuthData(AuthData authData) {
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
    public void assertAuthTknExists(String authToken) throws AuthTokenNotFoundException {
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
                break;
            }
        }
        if (deathRowAuthData == null) {
            return;
        }
        auths.remove(deathRowAuthData);
    }

    @Override
    public String getAuthTkn(String username) {
        for (var authData : auths) {
            if (authData.username().equals(username)) {
                return authData.authToken();
            }
        }
        return null;
    }

    @Override
    public String createAuth(String username) {
        String authToken = getAuthTkn(username);
        if (authToken != null) {
            return authToken;
        }
        var newAuthData = new AuthData(username);
        addAuthData(newAuthData);
        return newAuthData.authToken();
    }

    @Override
    public String getUsername(String authTkn) {
        for (var authData : auths) {
            if (authData.authToken().equals(authTkn)) {
                return authData.username();
            }
        }
        return null;
    }
}
