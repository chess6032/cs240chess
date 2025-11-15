package dataaccess.sqldao;

import chess.model.AuthData;
import dataaccess.AuthDAO;
import dataaccess.exceptions.SqlException;

import java.sql.ResultSet;

public class SqlAuthDAO extends SqlDAO implements AuthDAO {

    private final String AUTHTOKEN_HEADER = "auth";
    private final String USERNAME_HEADER = "username";

    public SqlAuthDAO() throws SqlException {
        super("auths");
    }

    @Override
    protected void configureDatabase() throws SqlException {
        super.configureDatabase("""
                CREATE TABLE IF NOT EXISTS %s (
                    %s VARCHAR(%d) NOT NULL PRIMARY KEY,
                    %s VARCHAR(%d) NOT NULL
                );
                """.formatted(TABLE_NAME,
                    AUTHTOKEN_HEADER, VAR_CHAR_SIZE,
                    USERNAME_HEADER, VAR_CHAR_SIZE)
        );
    }

    // UPDATES

    @Override
    public void clear() throws SqlException {
        clearTable();
    }

    @Override
    public String createAuth(String username) throws SqlException {
        final String authTkn = AuthData.generateAuthToken();
        final String sql = "INSERT INTO %s (%s, %s) VALUES (?, ?)".formatted(TABLE_NAME, AUTHTOKEN_HEADER, USERNAME_HEADER);
        executeUpdate(sql, authTkn, username);
        return authTkn;
    }

    @Override
    public boolean deleteAuth(String authToken) throws SqlException {
        String querySql = """
                SELECT * FROM %s
                WHERE %s = ?;
                """.formatted(TABLE_NAME, AUTHTOKEN_HEADER);

        if (!executeQuery(querySql, ResultSet::next, authToken)) {
            return false;
        }

        final String sql =
                """
                DELETE FROM %s
                WHERE %s = ?
                """.formatted(TABLE_NAME, AUTHTOKEN_HEADER);
        return executeUpdate(sql, authToken) == 0; // TODO: idk if this actually does anything ngl
    }

    // QUERIES

    @Override
    public int size() throws SqlException {
        return tableSize();
    }

    @Override
    public String findUserOfAuth(String authToken) throws SqlException {
        // FIXME: I don't think this is actually used in the SQL implementation???
        final String sql =
                """
                SELECT %s, %s
                FROM %s
                WHERE %s = ?;
                """.formatted(AUTHTOKEN_HEADER, USERNAME_HEADER,
                        TABLE_NAME,
                        AUTHTOKEN_HEADER);

        System.out.println(sql);
        String s = executeQuery(sql, (rs) -> {
           if (rs.next()) {
               return rs.getString(USERNAME_HEADER);
           }
           return null;
        }, authToken);
        System.out.println(s);
        return s;
    }
}
