package dataaccess.sqldao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.PasswordHasher;
import dataaccess.exceptions.SqlException;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public abstract class SqlDAO {

    protected static final int VAR_CHAR_SIZE = 255;

    protected final String TABLE_NAME;

    protected SqlDAO (String tableName) throws SqlException {
        TABLE_NAME = tableName;
        configureDatabase();
    }

    protected abstract void configureDatabase() throws SqlException;

    public void configureDatabase(String createStatement) throws SqlException {
        try {
            DatabaseManager.createDatabase();
        } catch(DataAccessException e) {
            throw new SqlException("DatabaseManager.createDatabase() failed. Exception message: " + (e.getMessage()));
        }
        try (Connection conn = DatabaseManager.getConnection()) { // try-with-resources
            try (var st = conn.createStatement()) {
                st.executeUpdate(createStatement);
            } catch (Exception e) {
                // FIXME: wtf do I catch???
                throw new RuntimeException(e); // FIXME: temporary
            }
        } catch (SQLException | DataAccessException e) {
            throw new SqlException(e.getMessage());
        }
    }

    protected int executeUpdate(String statement, Object... params) throws SqlException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                switch (param) {
                    case String p -> ps.setString(i + 1, p);
                    case Integer p -> ps.setInt(i + 1, p);
                    case null -> ps.setNull(i + 1, NULL);
                    default -> ps.setObject(i + 1, param);
                }
            }
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        } catch (SQLException e) {
            throw new SqlException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new SqlException(e);
        }
    }

    protected String hashPassword(String clearTextPassword) {
        return PasswordHasher.hashPassword(clearTextPassword);
    }

    protected boolean checkPassword(String clearTextPassword, String correctPassword) {
        return PasswordHasher.checkPassword(clearTextPassword, correctPassword);
    }

    protected <T> T executeQuery(String statement, ResultSetHandler<T> handler, Object... params) throws SqlException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                switch (param) {
                    case String p -> ps.setString(i + 1, p);
                    case Integer p -> ps.setInt(i + 1, p);
                    case null -> ps.setNull(i + 1, NULL);
                    default -> ps.setObject(i + 1, param);
                }
            }

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                return handler.handle(rs);
            }


        } catch (SQLException e) {
            throw new SqlException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new SqlException(e);
        }
    }

    protected void clearTable() throws SqlException {
        executeUpdate("DELETE FROM %s".formatted(TABLE_NAME));
    }

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

    protected int tableSize() throws SqlException {
        // query the size of the users table
        String sql = "SELECT COUNT(*) FROM %s".formatted(TABLE_NAME);
        return executeQuery(sql, (rs) -> {
            if (rs.next()) {
                return rs.getInt(1); // returns the count ig
            }
            return 0; // shouldn't happen for COUNT(*)
        });
    }
}