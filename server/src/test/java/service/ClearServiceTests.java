package service;

import model.UserData;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.MissingAttributeException;
import dataaccess.exceptions.SqlException;
import org.junit.jupiter.api.*;

public class ClearServiceTests extends ServiceTests {
    @Test
    @DisplayName("successful")
    public void clearSuccessful() {
        assertUserDAOsize(0);
        assertAuthDAOsize(0);

        // register a bunch of users
        for (int i = 0; i < 5; ++i) {
            try {
                userService.register(new UserData(Integer.toString(i), "password", "email"));
            } catch (AlreadyTakenException | MissingAttributeException | SqlException e) {
                throw new RuntimeException(e);
            }
        }

        assertUserDAOsize(5);
        assertAuthDAOsize(5);

        // register new user to get auth token
        String authToken;
        try {
            authToken = userService.register(new UserData("mario", "password", "email")).authToken();
        } catch (AlreadyTakenException | MissingAttributeException | SqlException e) {
            throw new RuntimeException(e);
        }

        assertUserDAOsize(6);
        assertAuthDAOsize(6);

        // create a bunch of games
        for (int i = 0; i < 3; ++i) {
            try {
                gameService.createGame(authToken, Integer.toString(i+1));
            } catch (AuthTokenNotFoundException | MissingAttributeException | SqlException e) {
                throw new RuntimeException(e);
            }
        }

        assertGameDAOsize(3);

        // clear everything
        try {
            userService.clear();
            gameService.clear();
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }

        // make sure everything's empty
        assertUserDAOsize(0);
        assertAuthDAOsize(0);
        assertGameDAOsize(0);
    }

    @Test
    @DisplayName("repeated clears")
    public void repeatedClearsSuccessful() {
        clearSuccessful();
        for (int i = 0; i < 10; ++i) {
            try {
                userService.clear();
                gameService.clear();
            } catch (SqlException e) {
                throw new RuntimeException(e);
            }
            assertUserDAOsize(0);
            assertAuthDAOsize(0);
            assertGameDAOsize(0);
        }
    }
}
