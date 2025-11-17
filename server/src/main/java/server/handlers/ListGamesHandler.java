package server.handlers;

import model.GameData;
import model.ListGamesResult;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.SqlException;
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

    public String handleListGamesRequest(Context ctx) throws AuthTokenNotFoundException, FailedSerializationException,
            SqlException {
        // get games list
        Collection<GameData> games = gameService.listGames(ctx.header("Authorization"));

        // prepare games list for serialization...
        HashSet<GameData> gamesCleaned = new HashSet<>(); // FIXME: does order matter??
        for (GameData game : games) {
            // don't include the ChessGame attribute in the HTTP result.
            gamesCleaned.add(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null));
        }
        return HandlerUtility.serialize(new ListGamesResult(gamesCleaned));
    }
}
