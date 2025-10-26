package dataaccess;

public interface AuthDAO {
    void clear();
    int size();
    String findAuthToken(String username);
    String createAuth(String username);
}
