package server.handlers;

import chess.model.GameData;
import chess.model.http.GameInfo;
import chess.model.http.ListGamesResult;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.exceptions.AuthTokenNotFoundException;
import io.javalin.http.Context;
import server.CommonResponses;

import java.util.ArrayList;

public class ListGamesHandler implements HTTPRequestHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ListGamesHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleRequest(Context ctx) {
        // deserialize request
        String authToken = ctx.header("Authorization");

        if (authToken == null) { // no authorization header TODO: implement this in other handlers
            // TODO: am I supposed to implement this??? Do I need to??????
            CommonResponses.BadRequestResponse(ctx); // 400
            return;
        }

        try {
            authDAO.assertAuthTknExists(authToken);
        } catch (AuthTokenNotFoundException e) {
            CommonResponses.UnauthorizedResponse(ctx); // 401
            return;
        }

        var gameInfos = new ArrayList<GameInfo>();
        for (GameData gameData : gameDAO.getAllGames()) {
            gameInfos.add(new GameInfo(gameData));
        }
        CommonResponses.SuccessResponse(ctx, new ListGamesResult(gameInfos));
    }
}