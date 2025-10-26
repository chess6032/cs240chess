package dataaccess;

import chess.model.UserData;

public interface UserDAO {
    void clear();
    int size();
    boolean createUser(String username, String password, String email);
    boolean userExists(String username);
    UserData getUser(String username);
}
