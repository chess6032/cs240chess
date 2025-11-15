package dataaccess.sqldao;

import chess.model.UserData;
import dataaccess.UserDAO;
import dataaccess.exceptions.SqlException;

public class SqlUserDAO extends SqlDAO implements UserDAO {

    private static final String USERNAME_HEADER = "username";
    private static final String PASSWORD_HEADER = "password";
    private static final String EMAIL_HEADER = "email";

    public SqlUserDAO() throws SqlException {
        super("users");
    }

    @Override
    protected void configureDatabase() throws SqlException {
        super.configureDatabase(
                """
                CREATE TABLE IF NOT EXISTS %s (
                    %s VARCHAR(%d) NOT NULL PRIMARY KEY,
                    %s VARCHAR(%d) NOT NULL,
                    %s VARCHAR(%d)
                );
                """.formatted(tableName,
                        USERNAME_HEADER, VAR_CHAR_SIZE,
                        PASSWORD_HEADER, VAR_CHAR_SIZE,
                        EMAIL_HEADER, VAR_CHAR_SIZE)
        );
    }

    // UPDATES

//    @Override
//    public void clear() throws SqlException {
//        executeUpdate("DELETE FROM %s".formatted(TABLE_NAME));
//    }

    @Override
    public void clear() throws SqlException {
        clearTable();
    }

    @Override
    public boolean createUser(String username, String clearTextPassword, String email) throws SqlException {
//        System.out.println(getUser(username));
        if (getUser(username) != null) {
            return false;
        }
        String hashedPassword = hashPassword(clearTextPassword);

        var sql = "INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)".formatted(tableName, USERNAME_HEADER, PASSWORD_HEADER, EMAIL_HEADER);
        return  executeUpdate(sql, username, hashedPassword, email) == 0;
    }

    // QUERIES

//    @Override
//    public int size() throws SqlException {
//        // query the size of the users table
//        String sql = "SELECT COUNT(*) FROM %s".formatted(TABLE_NAME);
//        return executeQuery(sql, (rs) -> {
//            if (rs.next()) {
//                return rs.getInt(1); // returns the count ig
//            }
//            return 0; // shouldn't happen for COUNT(*)
//        });
//    }

    @Override
    public int size() throws SqlException {
        return tableSize();
    }

    @Override
    public UserData getUser(String username) throws SqlException {
        final String sql =
                """
                SELECT %s, %s, %s
                FROM %s
                WHERE %s = ?
                """.formatted(USERNAME_HEADER, PASSWORD_HEADER, EMAIL_HEADER,
                        tableName,
                        USERNAME_HEADER);

        return executeQuery(sql, (rs) -> {
            // since we are looking for one row, we check rs.next() once.
            if (rs.next()) {
                String usernameUD = rs.getString(USERNAME_HEADER);
                String password = rs.getString(PASSWORD_HEADER);
                String email = rs.getString(EMAIL_HEADER);

                return new UserData(usernameUD, password, email);
            }

            // If rs.next() is false, no user was found with that username.
            return null;
        }, username);
    }

    @Override
    public boolean passwordMatches(String username, String clearTextPassword) throws SqlException {
        final String sql =
                """
                SELECT %s FROM %s WHERE %s = ?
                """.formatted(PASSWORD_HEADER, tableName, USERNAME_HEADER);

        String correctPassword = executeQuery(sql, (rs) -> {
           if (rs.next()) {
               return rs.getString(PASSWORD_HEADER);
           }
           return null;
        }, username);

        if (correctPassword == null) {
            return false; // username incorrect
        }

        return checkPassword(clearTextPassword, correctPassword);
    }

}
