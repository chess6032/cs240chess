package dataaccess.memorydao;

import chess.model.GameData;
import dataaccess.GameDAO;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public int size() {
        return games.size();
    }
}
