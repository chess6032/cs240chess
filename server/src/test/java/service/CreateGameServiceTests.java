package service;

import chess.model.http.CreateGameRequest;
import chess.model.http.RegisterRequest;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.BadRequestException;
import org.junit.jupiter.api.*;

public class CreateGameServiceTests extends ServiceTests {
    @Test
    @DisplayName("create game: unauthorized")
    public void createGameUnauthorized() {
        boolean exceptionThrown = false;

        // try creating game
        try {
            GameService.createGame(new CreateGameRequest("", "game"), server.getAuthDAO(), server.getGameDAO());
        } catch (AuthTokenNotFoundException e) {
            // good
            exceptionThrown = true;
        }
        Assertions.assertTrue(exceptionThrown);
    }

    @Test
    @DisplayName("create game: successful")
    public void createGameSuccessful() {
        var authDAO = server.getAuthDAO();
        var userDAO = server.getUserDAO();
        var gameDAO = server.getGameDAO();

        String authToken;
        // register user (to get auth token)
        try {
            authToken = UserService.register(
                    new RegisterRequest("username", "password", "email"),
                    userDAO, authDAO).authToken();
        } catch (AlreadyTakenException | BadRequestException e) {
            throw new RuntimeException(e);
        }

        try {
            GameService.createGame(new CreateGameRequest(authToken, "game"), authDAO, gameDAO);
        } catch (AuthTokenNotFoundException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(1, gameDAO.size());
    }
}
