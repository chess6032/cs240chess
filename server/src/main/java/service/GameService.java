package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.AuthTokenNotFoundException;

public record GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
    public void clear() {
        gameDAO.clear();
    }

    public int createGame(String authToken, String gameName) throws AuthTokenNotFoundException {
        if (authDAO.findUserOfAuth(authToken) == null) {
            throw new AuthTokenNotFoundException("GameService.createGame: unauthorized");
        }

        // FIXME: Does it matter if another game has the same name??

        return gameDAO.createGame(gameName);
    }
}
