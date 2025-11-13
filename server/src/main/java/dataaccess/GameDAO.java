package dataaccess;

import chess.model.GameData;
import dataaccess.exceptions.SqlException;

import java.util.Collection;

public interface GameDAO {
    void clear() throws SqlException;
    int size() throws SqlException;
    int createGame(String gameName);
    Collection<GameData> getAllGames() throws SqlException;
    GameData getGame(int gameID) throws SqlException;
    boolean addPlayerToGame(int gameID, String username, String playerColor);
}
