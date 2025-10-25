package dataaccess;

import chess.model.http.CreateGameResult;

public interface GameDAO {
    void clearGames();
    int size();

    CreateGameResult createGame(String gameName);
}
