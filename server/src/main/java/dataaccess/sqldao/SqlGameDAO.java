package dataaccess.sqldao;

import chess.model.GameData;
import dataaccess.GameDAO;
import dataaccess.exceptions.SqlException;

import java.util.ArrayList;
import java.util.Collection;

public class SqlGameDAO extends SqlDAO implements GameDAO {

    //              games meta
    // id | white username | black username | game name

    private final String GAME_ID_HEADER = "id";
    private final String WHITE_HEADER = "white_username";
    private final String BLACK_HEADER = "black_username";
    private final String GAME_NAME_HEADER = "name";

    //  games
    // id | game
//    private final String CHESSGAMES_TABLE_NAME = "games";
//    private final String CHESSGAME_HEADER = "game";
//    private final int CHESSGAME_JSON_STRING_SIZE = 10000;
//
    public SqlGameDAO() throws SqlException {
        super("games_meta");
    }

    @Override
    protected void configureDatabase() throws SqlException {
        super.configureDatabase("""
                CREATE TABLE IF NOT EXISTS %s (
                    %s INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    %s VARCHAR(%d),
                    %s VARCHAR(%d),
                    %s VARCHAR(%d) NOT NULL
                )
                """.formatted(
                        TABLE_NAME,
                        GAME_ID_HEADER,
                        WHITE_HEADER, VAR_CHAR_SIZE,
                        BLACK_HEADER, VAR_CHAR_SIZE,
                        GAME_NAME_HEADER, VAR_CHAR_SIZE//,
//                        CHESSGAME_HEADER, CHESS_GAME_JSON_STRING_SIZE
                )
        );
//        super.configureDatabase("""
//                CREATE TABLE IF NOT EXISTS %s (
//                    %s INT NOT NULL PRIMARY KEY,
//                    %s VARCHAR(%d) NOT NULL
//                )
//                """.formatted(CHESSGAMES_TABLE_NAME,
//                    GAME_ID_HEADER,
//                    CHESSGAME_HEADER, CHESSGAME_JSON_STRING_SIZE)
//        );
    }

    // UPDATES

    @Override
    public void clear() throws SqlException {
        clearTable();
    }

    @Override
    public int createGame(String gameName) throws SqlException {
        String DEFAULT_USERNAME_VALUE = "NULL";
        String sql = """
                INSERT INTO %s
                (%s, %s, %s)
                VALUES (%s, %s, ?)
                """.formatted(TABLE_NAME,
                WHITE_HEADER, BLACK_HEADER, GAME_NAME_HEADER,
                DEFAULT_USERNAME_VALUE, DEFAULT_USERNAME_VALUE);
//        System.out.println(sql);
//        System.out.println("game name: " + gameName);
        return executeUpdate(sql, gameName);
    }

    private boolean colorIsWhite(String playerColor) {
        return playerColor.equals("WHITE");
    }

    private boolean colorIsBlack(String playerColor) {
        return playerColor.equals("BLACK");
    }

    @Override
    public boolean addPlayerToGame(int gameID, String username, String playerColor) throws SqlException {
        GameData game = getGame(gameID);
        if (game == null) {
            return false;
        }

        // find column to insert username into
        String usernameHeader;
        if (colorIsWhite(playerColor)) {
            usernameHeader = WHITE_HEADER;
        } else if (colorIsBlack(playerColor)) {
            usernameHeader = BLACK_HEADER;
        } else {
            return false;
        }

        String sql = """
                UPDATE %s
                SET %s = ?
                WHERE %s = ?
                """.formatted(TABLE_NAME, usernameHeader, GAME_ID_HEADER);

        System.out.println(sql);

        executeUpdate(sql, username, gameID);

        return true;
    }

    // QUERIES

    @Override
    public int size() throws SqlException {
        return tableSize();
    }

    @Override
    public Collection<GameData> getAllGames() throws SqlException {
//        System.out.println("I need ALL the games");
        String sql = "SELECT * FROM %s".formatted(TABLE_NAME);
//        System.out.println(sql);
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
