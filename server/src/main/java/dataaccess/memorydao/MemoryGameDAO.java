package dataaccess.memorydao;

import chess.ChessGame;
import model.GameData;
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
        games.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    @Override
    public Collection<GameData> getAllGames() {
        return games.values();
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID); // returns null if game ID not found
    }

    @Override
    public boolean addPlayerToGame(int gameID, String username, String playerColor) {
        GameData gameData = games.get(gameID);
        if (!isColorAvailable(gameData, playerColor)) {
            return false;
        }

        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();

        if (playerColor.equals("WHITE")) {
            whiteUsername = username;
        } else if (playerColor.equals("BLACK")) {
            blackUsername = username;
        } else {
            throw new RuntimeException("how tf did we get here?");
        }

        games.put(gameID, new GameData(gameID, whiteUsername, blackUsername, gameData.gameName(), gameData.game()));
        return true;

        // FIXME: do I have to check if the user has already joined this game?
    }

    private boolean isColorAvailable(GameData gameData, String playerColor) {
        if (playerColor.equals("WHITE")) {
            return gameData.whiteUsername() == null;
        } else if (playerColor.equals("BLACK")) {
            return gameData.blackUsername() == null;
        }
        return false;
    }
}
