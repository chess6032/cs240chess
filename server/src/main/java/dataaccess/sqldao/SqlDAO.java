package dataaccess.sqldao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.*;

import static java.sql.Types.NULL;

public abstract class SqlDAO {
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


    // copied from petshop... ngl I have NO idea WTF is going on.
    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; ++i) {
                Object param = params[i];
                switch (param) {
                    case String p -> ps.setString(i + 1, p);
                    case Integer p -> ps.setInt(i + 1, p);
                    case null -> ps.setNull(i + 1, NULL);
                    default -> ps.setObject(i + 1, param);
                }
            }

            int updated = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return updated;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
