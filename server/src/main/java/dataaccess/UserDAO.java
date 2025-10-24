package dataaccess;

import chess.model.UserData;

public interface UserDAO {
    UserData getUser(String username);
    void createUser(UserData userData);
    void clearUsers();
}
