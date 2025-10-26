package dataaccess.MemoryDAO;

import chess.ChessGame;
import chess.model.GameData;
import chess.model.http.CreateGameResult;
import dataaccess.GameDAO;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {

    private final HashMap<Integer, GameData> games = new HashMap<>();
    int nextGameID = 0;

    @Override
    public void clearGames() {
        games.clear();
    }

    @Override
    public int size() {
        return games.size();
    }

    @Override
    public CreateGameResult createGame(String gameName) {
        var result = new CreateGameResult(nextGameID);
        games.put(nextGameID, new GameData(nextGameID, "", "", gameName, new ChessGame()));
        ++nextGameID;
        return result;
    }

    @Override
    public Collection<GameData> getAllGames() {
        return games.values();
    }
}