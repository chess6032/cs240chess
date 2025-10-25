package server.handlers;

import chess.model.http.CreateGameRequest;
import chess.model.http.CreateGameResult;
import chess.model.http.GameName;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.exceptions.AuthTokenNotFoundException;
import io.javalin.http.Context;
import server.CommonResponses;
import service.GameService;

import static server.CommonResponses.BadRequestResponse;

public class CreateGameHandler implements HTTPRequestHandler {
    private final Gson serializer = new Gson();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleRequest(Context ctx) {
        // deserialize request
        String authToken = ctx.header("Authorization");
        String gameName;
        try {
            gameName = serializer.fromJson(ctx.body(), GameName.class).gameName();
        } catch (JsonSyntaxException e) {
            BadRequestResponse(ctx); // 400
            return;
        }
        CreateGameRequest request = new CreateGameRequest(authToken, gameName);

        // fulfill request
        CreateGameResult result;
        try {
            result = GameService.createGame(request, authDAO, gameDAO);
        } catch (AuthTokenNotFoundException e) {
            CommonResponses.UnauthorizedResponse(ctx); // 401
            return;
        }
        CommonResponses.SuccessResponse(ctx, result); // 200
    }
}
