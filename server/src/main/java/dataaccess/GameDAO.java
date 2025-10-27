package dataaccess;

import chess.model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clear();
    int size();
    int createGame(String gameName);
    Collection<GameData> getAllGames();
    GameData getGame(int gameID);
    boolean addPlayerToGame(int gameID, String username, String playerColor);
}
