package dataaccess;

public interface AuthDAO {
    void clear();
    String findAuthToken(String username);
    String createAuth(String username);
}
