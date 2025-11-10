package dataaccess;

import chess.model.UserData;

public interface UserDAO {
    void clear() throws DataAccessException;
    int size() throws DataAccessException;
    boolean createUser(String username, String password, String email) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}
