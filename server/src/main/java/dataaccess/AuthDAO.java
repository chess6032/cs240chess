package dataaccess;

public interface AuthDAO {
    void clear();
    int size();
    String findAuthOfUser(String username);
    String createAuth(String username);
    String findUserOfAuth(String authToken);
    boolean deleteAuth(String authToken);
}
