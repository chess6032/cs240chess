package dataaccess.sqldao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import exception.ResponseException;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public abstract class SqlDAO {

    public SqlDAO () throws DataAccessException {
        configureDatabase();
    }

    protected abstract void configureDatabase() throws DataAccessException;

    public void configureDatabase(String createStatement) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) { // try-with-resources
            try (var st = conn.createStatement()) {
                st.executeUpdate(createStatement);
            } catch (Exception e) {
                // FIXME: wtf do I catch???
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    // From petshop. Use for UPDATING data. (NOT for querying.)
    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
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
            var resExc = new ResponseException(ResponseException.Code.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
            throw new DataAccessException(resExc.getMessage());
        }
    }

    protected String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    protected <T> T executeQuery(String statement, ResultSetHandler<T> handler, Object... params) throws DataAccessException {
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
            var resExc = new ResponseException(ResponseException.Code.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
            throw new DataAccessException(resExc.getMessage());
        }
    }
}
