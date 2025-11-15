package service;

import chess.model.UserData;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.MissingAttributeException;
import dataaccess.exceptions.SqlException;
import org.junit.jupiter.api.*;

public class CreateGameServiceTests extends ServiceTests {
    @Test
    @DisplayName("successful")
    public void createGameSuccessful() {

        // register user to get auth token

        String authToken;
        try {
            authToken = userService.register(new UserData("mario", "password", "email")).authToken();
        } catch (AlreadyTakenException | MissingAttributeException | SqlException e) {
            throw new RuntimeException(e);
        }

        try {
            gameService.createGame(authToken, "super brothers game");
        } catch (AuthTokenNotFoundException | MissingAttributeException | SqlException e) {
            throw new RuntimeException(e);
        }

        assertGameDAOsize(1);
    }

    @Test
    @DisplayName("unauthorized")
    public void createGameUnauthorized() {
        boolean exceptionThrown = false;

        try {
            gameService.createGame("skibidi", "gameName");
        } catch (AuthTokenNotFoundException e) {
            exceptionThrown = true;
        } catch (MissingAttributeException | SqlException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(exceptionThrown);
        assertGameDAOsize(0);
    }
}
