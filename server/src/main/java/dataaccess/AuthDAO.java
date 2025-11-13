package dataaccess;

import dataaccess.exceptions.SqlException;

public interface AuthDAO {
    void clear() throws SqlException;
    int size();
    String createAuth(String username) throws SqlException;
    String findUserOfAuth(String authToken);
    boolean deleteAuth(String authToken);
}
