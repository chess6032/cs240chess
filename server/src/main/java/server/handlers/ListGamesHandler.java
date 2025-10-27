package server.handlers;

import chess.model.GameData;
import chess.model.ListGamesResult;
import dataaccess.exceptions.AuthTokenNotFoundException;
import io.javalin.http.Context;
import server.FailedSerializationException;
import service.GameService;

import java.util.Collection;
import java.util.HashSet;

public class ListGamesHandler {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public String handleListGamesRequest(Context ctx) throws AuthTokenNotFoundException, FailedSerializationException {
        // get games list
        Collection<GameData> games = gameService.listGames(ctx.header("Authorization"));

        // prepare games list for serialization...
        HashSet<GameData> gamesCleaned = new HashSet<>(); // FIXME: does order matter??
        for (GameData game : games) {
            // white/black usernames might be null. convert them to empty strings so they show up in the HTTP result.
            String whiteUsername = game.whiteUsername();
            if (whiteUsername == null) {
                whiteUsername = "";
            }
            String blackUsername = game.blackUsername();
            if (blackUsername == null) {
                blackUsername = "";
            }
            // don't include the ChessGame attribute in the HTTP result.
            gamesCleaned.add(new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), null));
        }
        return HandlerUtility.serialize(new ListGamesResult(gamesCleaned));
    }
}
