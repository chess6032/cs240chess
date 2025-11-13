package dataaccess.sqldao;

import chess.model.GameData;
import dataaccess.GameDAO;
import dataaccess.exceptions.SqlException;

import java.util.Collection;
import java.util.List;

public class SqlGameDAO extends SqlDAO implements GameDAO {

    private final String GAME_ID_HEADER = "id";
    private final String CHESSGAME_HEADER = "game";
    private final int ChessGameJsonStringSize = 10000;
    private final String WHITE_HEADER = "`white username`";
    private final String BLACK_HEADER = "`black username`";
    private final String GAME_NAME_HEADER = "`game name`";

    protected SqlGameDAO() throws SqlException {
        super("games");
    }

    @Override
    protected void configureDatabase() throws SqlException {
        super.configureDatabase("""
                CREATE TABLE IF NOT EXISTS %s (
                    %s INT NOT NULL PRIMARY KEY,
                    %s VARCHAR(%d) NOT NULL,
                    %s VARCHAR(%d),
                    %s VARCHAR(%d),
                    %s VARCHAR(%d) NOT NULL
                """.formatted(
                        TABLE_NAME,
                        GAME_ID_HEADER,
                        CHESSGAME_HEADER, ChessGameJsonStringSize,
                        WHITE_HEADER, VAR_CHAR_SIZE,
                        BLACK_HEADER, VAR_CHAR_SIZE,
                        GAME_NAME_HEADER, VAR_CHAR_SIZE)
        );
    }

    // UPDATES

    @Override
    public void clear() throws SqlException {
        clearTable();
    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public boolean addPlayerToGame(int gameID, String username, String playerColor) {
        return false;
    }

    // QUERIES

    @Override
    public int size() throws SqlException {
        return tableSize();
    }


    @Override
    public Collection<GameData> getAllGames() throws SqlException {
        String sql = "SELECT * FROM %s".formatted(TABLE_NAME);
        return executeQuery(sql, (rs) -> {
           return null; // stub
        });
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }
}
