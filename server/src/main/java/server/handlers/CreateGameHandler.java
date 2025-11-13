package server.handlers;

import chess.model.GameData;
import com.google.gson.JsonSyntaxException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.MissingAttributeException;
import dataaccess.exceptions.SqlException;
import io.javalin.http.Context;
import server.FailedDeserializationException;
import server.FailedSerializationException;
import service.GameService;
import service.UserService;

public class CreateGameHandler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public String handleCreateGameRequest(Context ctx) throws FailedDeserializationException, MissingAttributeException,
            AuthTokenNotFoundException, FailedSerializationException, SqlException {

        String gameName = HandlerUtility.deserializeBody(ctx, GameData.class).gameName();
        String authToken = ctx.header("Authorization");

        int gameID = gameService.createGame(authToken, gameName); // throws AuthTokenNotFoundException, MissingAttributeException

        return HandlerUtility.serialize(new GameData(gameID, null, null, null, null));
    }
}
