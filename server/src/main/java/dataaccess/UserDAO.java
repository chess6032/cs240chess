package dataaccess;

import chess.model.UserData;
import dataaccess.exceptions.SqlException;

public interface UserDAO {
    void clear() throws SqlException;
    int size() throws SqlException;
    boolean createUser(String username, String password, String email) throws SqlException;
    UserData getUser(String username) throws SqlException;
}
