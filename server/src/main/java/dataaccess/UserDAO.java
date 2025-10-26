package dataaccess;

import chess.model.UserData;

public interface UserDAO {
    void clear();
    int size();
    boolean createUser(String username, String password, String email);
    UserData getUser(String username);
}
