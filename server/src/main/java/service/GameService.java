package service;

import chess.model.GameData;
import chess.model.http.CreateGameRequest;
import chess.model.http.CreateGameResult;
import chess.model.http.JoinGameRequest;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.GameNotFoundException;
import io.javalin.http.BadRequestResponse;

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

    static void joinGame(JoinGameRequest joinGameRequest, AuthDAO authDAO, GameDAO gameDAO)
            throws AuthTokenNotFoundException, GameNotFoundException, AlreadyTakenException, BadRequestException {

        authDAO.assertAuthTknExists(joinGameRequest.authToken()); // throws AuthTokenNotFoundException

        var game = gameDAO.getGame(joinGameRequest.gameID());
        if (game == null) {
            throw new GameNotFoundException("GameService.joinGame: game not found");
        }

        String whiteUsername = null;
        String blackUsername = null;

        String color = joinGameRequest.playerColor();
        if (color.equals("WHITE")) {
            if (!game.whiteUsername().isEmpty()) {
                throw new AlreadyTakenException("GameService.joinGame: WHITE already has corresponding username");
            }
            whiteUsername = authDAO.getUsername(joinGameRequest.authToken());
        } else if (color.equals("BLACK")) {
            if (!game.blackUsername().isEmpty()) {
                throw new AlreadyTakenException("GameService.joinGame: BLACK already has corresponding username");
            }
            blackUsername = authDAO.getUsername(joinGameRequest.authToken());
        } else {
            throw new BadRequestException("GameService.joinGame: inputted player color was neither 'WHITE' nor 'BLACK'");
        }

        if (whiteUsername == null) {
            whiteUsername = game.whiteUsername();
        }
        if (blackUsername == null) {
            blackUsername = game.blackUsername();
        }

        gameDAO.updateGame(new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game()));
    }
}
