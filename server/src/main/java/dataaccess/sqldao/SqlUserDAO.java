package dataaccess.sqldao;

import chess.model.UserData;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import org.mindrot.jbcrypt.BCrypt;

public class SqlUserDAO extends SqlDAO implements UserDAO {

    public SqlUserDAO() throws DataAccessException {
        super();
    }

    @Override
    public void configureDatabase() throws DataAccessException {
        // TODO: does username need to be NOT NULL

        super.configureDatabase(
                """
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(255) NOT NULL PRIMARY KEY,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255)
                );
                """
        );
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("DELETE FROM user");
        assert true; // TODO: assert that the size of user is 0
    }

    @Override
    public int size() {
        // query the size of the users table
        return -1;
    }

    @Override
    public boolean createUser(String username, String clearTextPassword, String email) {
        String hashedPassword = hashPassword(clearTextPassword);
        try {
            saveUserData(username, hashedPassword, email);
        } catch (Exception e) {
            // TODO: what exception? do I even catch an exception actually??
            return false;
        }
        return true;
    }

    @Override
    public UserData getUser(String username) {

        return null;
    }

    private String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    private void saveUserData(String username, String password, String email) {
        // password should already be encrypted!!!!

    }
}
