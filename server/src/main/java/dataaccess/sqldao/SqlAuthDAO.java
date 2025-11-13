package dataaccess.sqldao;

import chess.model.AuthData;
import dataaccess.AuthDAO;
import dataaccess.exceptions.SqlException;

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
        String authTkn = AuthData.generateAuthToken();
        String sql = "INSERT INTO %s (%s, %s) VALUES (?, ?)".formatted(TABLE_NAME, AUTHTOKEN_HEADER, USERNAME_HEADER);
        executeUpdate(sql, authTkn, username);
        return authTkn;
    }

    @Override
    public boolean deleteAuth(String authToken) {
        return false;
    }

    // QUERIES

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String findUserOfAuth(String authToken) {
        return "";
    }
}
