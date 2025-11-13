package dataaccess.sqldao;

import chess.model.GameData;
import dataaccess.GameDAO;
import dataaccess.exceptions.SqlException;

import java.util.ArrayList;
import java.util.Collection;

public class SqlGameDAO extends SqlDAO implements GameDAO {

    private final String GAME_ID_HEADER = "id";
    private final String WHITE_HEADER = "`white username`";
    private final String BLACK_HEADER = "`black username`";
    private final String GAME_NAME_HEADER = "`game name`";
    private final String CHESSGAME_HEADER = "game";
    private final int CHESS_GAME_JSON_STRING_SIZE = 10000;

    public SqlGameDAO() throws SqlException {
        super("games");
    }

    @Override
    protected void configureDatabase() throws SqlException {
        super.configureDatabase("""
                CREATE TABLE IF NOT EXISTS %s (
                    %s INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    %s VARCHAR(%d),
                    %s VARCHAR(%d),
                    %s VARCHAR(%d) NOT NULL,
                    %s VARCHAR(%d) NOT NULL
                )
                """.formatted(
                        TABLE_NAME,
                        GAME_ID_HEADER,
                        WHITE_HEADER, VAR_CHAR_SIZE,
                        BLACK_HEADER, VAR_CHAR_SIZE,
                        GAME_NAME_HEADER, VAR_CHAR_SIZE,
                        CHESSGAME_HEADER, CHESS_GAME_JSON_STRING_SIZE
                )
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
            Collection<GameData> games = new ArrayList<>();
            while (rs.next()) {
                int _gameID = Integer.parseInt(rs.getString(GAME_ID_HEADER));
                String _whiteUsername = rs.getString(WHITE_HEADER);
                String _blackUsername = rs.getString(BLACK_HEADER);
                String _gameName = rs.getString(GAME_NAME_HEADER);
                games.add(new GameData(_gameID, _whiteUsername, _blackUsername, _gameName, null));
            }
            return games;
        });
    }

    @Override
    public GameData getGame(int gameID) throws SqlException {
        String sql = "SELECT * FROM %s WHERE %s = ?".formatted(TABLE_NAME, GAME_ID_HEADER);
        return executeQuery(sql, (rs) -> {
            if (rs.next()) {
                int _gameID = Integer.parseInt(rs.getString(GAME_ID_HEADER));
                String _whiteUsername = rs.getString(WHITE_HEADER);
                String _blackUsername = rs.getString(BLACK_HEADER);
                String _gameName = rs.getString(GAME_NAME_HEADER);
                return new GameData(_gameID, _whiteUsername, _blackUsername, _gameName, null);
            }
            return null;
        }, gameID);
    }
}
