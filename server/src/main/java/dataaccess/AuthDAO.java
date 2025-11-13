package dataaccess;

import dataaccess.exceptions.SqlException;

public interface AuthDAO {
    void clear() throws SqlException;
    int size() throws SqlException;
    String createAuth(String username) throws SqlException;
    String findUserOfAuth(String authToken) throws SqlException;
    boolean deleteAuth(String authToken) throws SqlException;
}
