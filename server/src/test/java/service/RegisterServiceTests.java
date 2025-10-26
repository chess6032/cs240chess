package service;

import chess.model.UserData;
import dataaccess.exceptions.AlreadyTakenException;
import org.junit.jupiter.api.*;

public class RegisterServiceTests extends ServiceTests {

    private void assertDAOsize(int expected) {
        assertUserDAOsize(expected);
        assertAuthDAOsize(expected);
    }

    @Test
    @DisplayName("successful")
    public void registerSuccessful() {
        assertDAOsize(0);
        try {
            userService.register(new UserData("mario", "peachy", "superm@egadd.com"));
            assertDAOsize(1);

            userService.register(new UserData("luigi", "i<3bro", "weegee@egadd.com"));
            assertDAOsize(2);

            userService.register(new UserData("bowser jr.", "password", "bigboss@evil.com"));
            assertDAOsize(3);

            userService.register(new UserData("bowser", "peachy", "kingkoopa@evil.com"));
            assertDAOsize(4);

            userService.register(new UserData("username", "password", "kingkoopa@evil.com"));
            assertDAOsize(5);
        } catch (AlreadyTakenException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    @DisplayName("username already taken")
    public void registerUsernameAlreadyTaken() {
        assertDAOsize(0);
        boolean exceptionThrown = false;

        // SUCCESSFULLY register users
        try {
            userService.register(new UserData("mario", "password", "email"));
            assertDAOsize(1);

            userService.register(new UserData("luigi", "password", "abc")); // users MAY have the same PASSWORD
            assertDAOsize(2);

            userService.register(new UserData("peach", "abc", "email")); // users MAY have the same EMAIL
            assertDAOsize(3);

            userService.register(new UserData("daisy", "password", "email")); // users MAY have the same PASSWORD AND EMAIL
            assertDAOsize(4);
        } catch (AlreadyTakenException e) {
            throw new RuntimeException(e);
        }

        // UNSUCCESSFULLY register user
        try {
            userService.register(new UserData("mario", "abcdefg", "hijklmnop"));
        } catch (AlreadyTakenException e) {
            exceptionThrown = true;
        }

        Assertions.assertTrue(exceptionThrown);
    }
}
