package dataaccess;

import model.UserData;
import dataaccess.exceptions.SqlException;

public interface UserDAO {
    void clear() throws SqlException;
    int size() throws SqlException;
    boolean createUser(String username, String password, String email) throws SqlException;
    UserData getUser(String username) throws SqlException;
    boolean passwordMatches(String username, String password) throws SqlException;
}
