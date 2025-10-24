package dataaccess;

import chess.model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);
    void clearAuths();
    int size();
    boolean hasUser(String username);
}
