package dataaccess;

import chess.ChessGame;
import model.GameData;
import dataaccess.exceptions.SqlException;
import server.FailedSerializationException;

import java.util.Collection;

public interface GameDAO {
    void clear() throws SqlException;
    int size() throws SqlException;
    int createGame(String gameName) throws SqlException;
    Collection<GameData> getAllGames() throws SqlException;
    GameData getGame(int gameID) throws SqlException;
    boolean addPlayerToGame(int gameID, String username, String playerColor) throws SqlException;
    boolean removePlayerFromGame(int gameID, String username) throws SqlException;
    void setGame(int gameID, ChessGame game) throws SqlException;
}
