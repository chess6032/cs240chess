package dataaccess;

import chess.model.GameData;
import chess.model.http.CreateGameResult;

import java.util.Collection;

public interface GameDAO {
    void clearGames();
    int size();

    CreateGameResult createGame(String gameName);
    Collection<GameData> getAllGames();
}
