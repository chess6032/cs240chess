package service;

import model.GameData;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.*;

import java.util.Collection;

public record GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
    public void clear() throws SqlException {
        gameDAO.clear();
    }

    public int createGame(String authToken, String gameName) throws AuthTokenNotFoundException, MissingAttributeException, SqlException {
        if (gameName == null) {
            throw new MissingAttributeException("GameService.createGame: gameName is null");
        }

        if (authDAO.findUserOfAuth(authToken) == null) {
            throw new AuthTokenNotFoundException("GameService.createGame: auth token not found: " + authToken);
        }

        // FIXME: Does it matter if another game has the same name??

        return gameDAO.createGame(gameName);
    }

    public Collection<GameData> listGames(String authToken) throws AuthTokenNotFoundException, SqlException {
        if (authDAO.findUserOfAuth(authToken) == null) {
            throw new AuthTokenNotFoundException("GameService.listGames: auth token not found: " + authToken);
        }

        return gameDAO.getAllGames();
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws AuthTokenNotFoundException, GameNotFoundException,
            AlreadyTakenException, MissingAttributeException, SqlException {

//        if (playerColor != null) {
            if (playerColor == null ||  gameID <= 0 ||
                    !(playerColor.equals("WHITE") || playerColor.equals("BLACK"))) {
                throw new MissingAttributeException("GameService.joinGame: mistyped player color or game ID. " +
                        "player color: " + playerColor + ", gameID: " + gameID);
            }
//        }


        String username = authDAO.findUserOfAuth(authToken);
        if (username == null) {
            throw new AuthTokenNotFoundException("GameService.joinGame: auth token not found" + authToken);
        }

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new GameNotFoundException("GameService.joinGame: game w/ ID not found: " + gameID);
        }

//        if (playerColor != null) {
            // playerColor == null means we're getting an observer who wants to join the game
            // PSYCH observers are never supposed to call joinGame
            if (!gameDAO.addPlayerToGame(gameID, username, playerColor)) {
                throw new AlreadyTakenException("GameService.joinGame: gameID = " + gameID + ". Color already taken: " + playerColor);
            }
//        }


        // FIXME: do I need to check if player has already joined this game?
    }
}
