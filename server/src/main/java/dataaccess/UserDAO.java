package dataaccess;

public interface UserDAO {
    void clear();
    boolean createUser(String username, String password, String email);
    boolean userExists(String username);
}
