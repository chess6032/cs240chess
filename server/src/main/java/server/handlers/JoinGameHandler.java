package server.handlers;

import chess.model.PlayerColorGameIDforJSON;
import dataaccess.exceptions.*;
import io.javalin.http.Context;
import server.FailedDeserializationException;
import service.GameService;
import service.UserService;

import static server.handlers.HandlerUtility.deserializeBody;

public class JoinGameHandler {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handleJoinGameRequest(Context ctx) throws FailedDeserializationException, MissingAttributeException,
            AuthTokenNotFoundException, AlreadyTakenException, GameNotFoundException, SqlException {

        var info = deserializeBody(ctx, PlayerColorGameIDforJSON.class);
        String authToken = ctx.header("Authorization");
        String playerColor = info.playerColor();
        int gameID = info.gameID();

        gameService.joinGame(authToken, playerColor, gameID);
    }
}
