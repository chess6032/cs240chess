package dataaccess.sqldao;

import server.ChessGameSerializer;
import model.GameData;
import dataaccess.GameDAO;
import dataaccess.exceptions.SqlException;
import server.FailedSerializationException;

import java.util.ArrayList;
import java.util.Collection;

public class SqlGameDAO extends SqlDAO implements GameDAO {

    //              games_meta
    // id | white_username | black_username | game_name

    private static final String GAME_ID_HEADER = "id";
    private static final String WHITE_HEADER = "white_username";
    private static final String BLACK_HEADER = "black_username";
    private static final String GAME_NAME_HEADER = "name";

    // TODO: make games table (for storing serialized ChessGame objects)

    //  games
    // id | game
    private static final String CHESSGAME_TABLE_NAME = "games";
    private static final String CHESSGAME_HEADER = "game";

    public SqlGameDAO() throws SqlException {
        super("games_meta");
    }

    @Override
    protected void configureDatabase() throws SqlException {
        // GAMES META
        super.configureDatabase("""
                CREATE TABLE IF NOT EXISTS %s (
                    %s INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    %s VARCHAR(%d),
                    %s VARCHAR(%d),
                    %s VARCHAR(%d) NOT NULL
                )
                """.formatted(
                        tableName,
                        GAME_ID_HEADER,
                        WHITE_HEADER, VAR_CHAR_SIZE,
                        BLACK_HEADER, VAR_CHAR_SIZE,
                        GAME_NAME_HEADER, VAR_CHAR_SIZE
                )
        );

        // GAMES (Holds JSON-serialized ChessGame objects)
        super.configureDatabase("""
                CREATE TABLE IF NOT EXISTS %s (
                    %s INT NOT NULL PRIMARY KEY,
                    %s TEXT
                )
                """.formatted(
                        CHESSGAME_TABLE_NAME,
                        GAME_ID_HEADER,
                        CHESSGAME_HEADER
                ));
    }

    // UPDATES

    @Override
    public void clear() throws SqlException {
        clearTable();
    }

    @Override
    public int createGame(String gameName) throws SqlException {
        if (gameName == null) {
            return -1;
            // TODO/FIXME: I should make sure the other DAO's methods check if null is inputted for a column that can't be null
        }

        String defaultUsernameValue = "NULL";
        String newChessGameJson = "NULL";
        try {
            newChessGameJson = ChessGameSerializer.newChessGameJson();
        } catch (FailedSerializationException e) {
            throw new RuntimeException(e);
        }

        String sql1 = """
                INSERT INTO %s
                (%s, %s, %s)
                VALUES (%s, %s, ?);
                """.formatted(tableName,
                WHITE_HEADER, BLACK_HEADER, GAME_NAME_HEADER,
                defaultUsernameValue, defaultUsernameValue
                );
        String sql2 = """
                INSERT INTO %s
                (%s, %s)
                VALUES
                (?, ?);
                """.formatted(CHESSGAME_TABLE_NAME,
                GAME_ID_HEADER, CHESSGAME_HEADER
                );
        int id = executeUpdate(sql1, gameName);
        executeUpdate(sql2, id, newChessGameJson);
        return id;
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
            if (game.whiteUsername() != null) {
                return false;
            }
            usernameHeader = WHITE_HEADER;
        } else if (colorIsBlack(playerColor)) {
            if (game.blackUsername() != null) {
                return false;
            }
            usernameHeader = BLACK_HEADER;
        } else {
            return false;
        }

        String sql = """
                UPDATE %s
                SET %s = ?
                WHERE %s = ?
                """.formatted(tableName, usernameHeader, GAME_ID_HEADER);

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
        String sql = "SELECT * FROM %s".formatted(tableName);
        return executeQuery(sql, (rs) -> {
            Collection<GameData> games = new ArrayList<>();
            while (rs.next()) {
                int gameID = Integer.parseInt(rs.getString(GAME_ID_HEADER));
                String whiteUsername = rs.getString(WHITE_HEADER);
                String blackUsername = rs.getString(BLACK_HEADER);
                String gameName = rs.getString(GAME_NAME_HEADER);
                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, null));
            }
            return games;
        });
    }

    @Override
    public GameData getGame(int gameID) throws SqlException {
        String sql = "SELECT * FROM %s WHERE %s = ?".formatted(tableName, GAME_ID_HEADER);
        return executeQuery(sql, (rs) -> {
            if (rs.next()) {
                int gameIDGD = Integer.parseInt(rs.getString(GAME_ID_HEADER));
                String whiteUsername = rs.getString(WHITE_HEADER);
                String blackUsername = rs.getString(BLACK_HEADER);
                String gameName = rs.getString(GAME_NAME_HEADER);
                return new GameData(gameIDGD, whiteUsername, blackUsername, gameName, null);
            }
            return null;
        }, gameID);
    }

    public static void main(String[] args) {
        try {
            var dao = new SqlGameDAO();
            int id = dao.createGame("skibidi");
            System.out.print(dao.getGame(id));
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }
}
