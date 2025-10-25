package dataaccess;

import chess.model.AuthData;
import dataaccess.exceptions.AuthTokenNotFoundException;

public interface AuthDAO {
    void addAuthData(AuthData authData);
    String createAuth(String username);
    void clearAuths();
    int size();
    boolean hasUser(String username);
    String getUsername(String authTkn);
    void assertAuthTknExists(String authToken) throws AuthTokenNotFoundException;
    void deleteAuth(String authToken);
    String getAuthTkn(String username);
}
