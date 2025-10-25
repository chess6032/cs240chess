package service;

import chess.model.http.CreateGameRequest;
import chess.model.http.CreateGameResult;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.exceptions.AuthTokenNotFoundException;

public interface GameService {
    static void clearGames(GameDAO gameDAO) {
        gameDAO.clearGames();
    }

    static CreateGameResult createGame(CreateGameRequest request, AuthDAO authDAO, GameDAO gameDAO) throws AuthTokenNotFoundException {
        String authToken = request.authToken();
        String gameName = request.gameName();

        authDAO.assertAuthTknExists(authToken); // throws AuthTokenNotFoundException

        return gameDAO.createGame(gameName);
    }
}
