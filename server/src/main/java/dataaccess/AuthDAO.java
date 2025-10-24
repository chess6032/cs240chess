package dataaccess;

import chess.model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);
    void clearAuths();
    int size();
    boolean hasUser(String username);
    void assertAuthExists(String authToken) throws AuthTokenNotFoundException;
    void deleteAuth(String authToken);
}
