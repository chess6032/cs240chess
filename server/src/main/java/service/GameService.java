package service;

import chess.model.GameData;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.AuthTokenNotFoundException;

import java.util.Collection;

public record GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
    public void clear() {
        gameDAO.clear();
    }

    public int createGame(String authToken, String gameName) throws AuthTokenNotFoundException {
        if (authDAO.findUserOfAuth(authToken) == null) {
            throw new AuthTokenNotFoundException("GameService.createGame: auth token not found: " + authToken);
        }

        // FIXME: Does it matter if another game has the same name??

        return gameDAO.createGame(gameName);
    }

    public Collection<GameData> listGames(String authToken) throws AuthTokenNotFoundException {
        if (authDAO.findUserOfAuth(authToken) == null) {
            throw new AuthTokenNotFoundException("GameService.listGames: auth token not found: " + authToken);
        }

        return gameDAO.getAllGames();
    }
}
