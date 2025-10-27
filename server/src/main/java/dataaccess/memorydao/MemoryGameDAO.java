package dataaccess.memorydao;

import chess.ChessGame;
import chess.model.GameData;
import dataaccess.GameDAO;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private final HashMap<Integer, GameData> games = new HashMap<>();
    static int nextGameID = 1;

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public int size() {
        return games.size();
    }

    @Override
    public int createGame(String gameName) {
        int gameID = nextGameID++;
        games.put(gameID, new GameData(gameID, "", "", gameName, new ChessGame()));
        return gameID;
    }

    @Override
    public Collection<GameData> getAllGames() {
        return games.values();
    }
}
