package dataaccess.MemoryDAO;

import chess.ChessGame;
import chess.model.http.CreateGameResult;
import dataaccess.GameDAO;
import kotlin.Pair;

import java.util.HashMap;
import java.util.UUID;

public class MemoryGameDAO implements GameDAO {

    private final HashMap<Integer, Pair<String, ChessGame>> games = new HashMap<>();
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
        games.put(result.gameID(), new Pair<>(gameName, new ChessGame()));
        ++nextGameID;
        return result;
    }
}
