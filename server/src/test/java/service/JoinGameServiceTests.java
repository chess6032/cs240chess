package service;

import chess.model.UserData;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.GameNotFoundException;
import dataaccess.exceptions.MissingAttributeException;
import org.junit.jupiter.api.*;

public class JoinGameServiceTests extends ServiceTests {
    @Test
    @DisplayName("successful")
    public void joinGameSuccessful() {
        // register user to get auth token
        String authToken;
        try {
            authToken = userService.register(new UserData("mario", "password", "email")).authToken();
        } catch (AlreadyTakenException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        // make a bunch of games
        for (int i = 0; i < 5; ++i) {
            gameDAO.createGame(Integer.toString(i+1));
        }

        assertGameDAOsize(5);

        // join each game
        // FIXME: should a user only be able to join one game at a time?
        for (int i = 0; i < 5; ++i) {
            try {
                gameService.joinGame(authToken, "WHITE", i+1);
            } catch (AuthTokenNotFoundException | AlreadyTakenException | MissingAttributeException | GameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @DisplayName("unauthorized")
    public void joinGameUnauthorized() {
        boolean exceptionThrown = false;

        try {
            gameService.joinGame("", "WHITE", 12);
        } catch (AuthTokenNotFoundException e) {
            exceptionThrown = true;
        } catch (AlreadyTakenException | MissingAttributeException | GameNotFoundException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(exceptionThrown);
        assertGameDAOsize(0);
    }
}
