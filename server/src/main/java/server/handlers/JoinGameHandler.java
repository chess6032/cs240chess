package server.handlers;

import chess.model.http.JoinGameRequest;
import chess.model.http.PlayerColorAndGameID;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.GameNotFoundException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import server.CommonResponses;
import service.GameService;

public class JoinGameHandler implements HTTPRequestHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleRequest(Context ctx) {
        String authToken = ctx.header("Authorization");
        PlayerColorAndGameID info;
        try {
            info = new Gson().fromJson(ctx.body(), PlayerColorAndGameID.class);
        } catch (JsonSyntaxException e) {
            CommonResponses.BadRequestResponse(ctx);
            return;
        }
        try {
            GameService.joinGame(new JoinGameRequest(authToken, info.playerColor(), info.gameID()), authDAO, gameDAO);
        } catch (AuthTokenNotFoundException e) {
            CommonResponses.UnauthorizedResponse(ctx);
            return;
        } catch (AlreadyTakenException e) {
            CommonResponses.AlreadyTakenResponse(ctx);
            return;
        } catch (GameNotFoundException e) {
            CommonResponses.buildResponse(ctx, CommonResponses.UNAUTHORIZED_STATUS, "Error: game not found");
            return;
        } catch (BadRequestException e) {
            CommonResponses.BadRequestResponse(ctx);
            return;
        }

        CommonResponses.EmptySuccessResponse(ctx);
    }
}
