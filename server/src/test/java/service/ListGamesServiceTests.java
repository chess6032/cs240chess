package service;

import chess.model.GameData;
import chess.model.UserData;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.MissingAttributeException;
import org.junit.jupiter.api.*;

import java.util.Collection;

public class ListGamesServiceTests extends ServiceTests {
    @Test
    @DisplayName("successful")
    public void listGamesSuccessful() {
        // register user to get auth token

        String authToken;
        try {
            authToken = userService.register(new UserData("mario", "password", "email")).authToken();
        } catch (AlreadyTakenException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        // create a bunch of games
        for (int i = 0; i < 5; ++i) {
            try {
                gameService.createGame(authToken, Integer.toString(i+1));
            } catch (AuthTokenNotFoundException | MissingAttributeException e) {
                throw new RuntimeException(e);
            }
        }
        assertGameDAOsize(5);

        // get games list
        Collection<GameData> gamesList;
        try {
            gamesList = gameService.listGames(authToken);
        } catch (AuthTokenNotFoundException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(gameDAO.size(), gamesList.size());
    }

    @Test
    @DisplayName("unauthorized")
    public void listGamesUnauthorized() {
        boolean exceptionThrown = false;

        try {
            gameService.listGames("skibidi rizz");
        } catch (AuthTokenNotFoundException e) {
            exceptionThrown = true;
        }

        Assertions.assertTrue(exceptionThrown);
    }
}