package service;

import model.UserData;
import dataaccess.exceptions.*;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.HashSet;

public class LogoutServiceTests extends ServiceTests {

    private void registerAndLogout(Collection<UserData> users, Collection<String> authTokens) throws SqlException {
        // register users

        users.add(new UserData("mario", "peachy", "superm@egadd.com"));
        users.add(new UserData("luigi", "i<3bro", "weegee@egadd.com"));
        users.add(new UserData("bowser jr.", "password", "bigboss@evil.com"));
        users.add(new UserData("bowser", "peachy", "kingkoopa@evil.com"));
        users.add(new UserData("username", "password", "kingkoopa@evil.com"));

        int expectedUserDAOsize = 0;
        int expectedAuthDAOsize = 0;

        try {
            for (var userData : users) {
                authTokens.add(userService.register(userData).authToken());
                assertUserDAOsize(++expectedUserDAOsize);
                assertAuthDAOsize(++expectedAuthDAOsize);
            }
        } catch (AlreadyTakenException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        // (check to make sure I wrote test right)
        Assertions.assertEquals(users.size(), expectedUserDAOsize);
        Assertions.assertEquals(authTokens.size(), expectedAuthDAOsize);

        // log out users

        try {
            for (var authToken : authTokens) {
                userService.logout(authToken);
                assertUserDAOsize(expectedUserDAOsize); // number of registered users should NOT change.
                assertAuthDAOsize((--expectedAuthDAOsize)); // number of auth tokens SHOULD decrease.
            }
        } catch (AuthTokenNotFoundException e) {
            throw new RuntimeException(e);
        }

        assertUserDAOsize(expectedUserDAOsize);
        assertAuthDAOsize(0);
        Assertions.assertEquals(0, expectedAuthDAOsize); // (just in case I wrote the test wrong)
    }

    @Test
    @DisplayName("successful: register -> logout")
    public void logoutSuccessful() {
        HashSet<UserData> users = new HashSet<>();
        HashSet<String> authTokens = new HashSet<>();
        try {
            registerAndLogout(users, authTokens);
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("successful: register -> log out -> log in -> log out")
    public void logoutLoginLogoutSuccessful() {

        HashSet<UserData> users = new HashSet<>();
        HashSet<String> authTokens = new HashSet<>();
        try {
            registerAndLogout(users, authTokens);
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }

        int expectedUserDAOsize = users.size();
        Assertions.assertTrue(expectedUserDAOsize > 0);
        int expectedAuthDAOsize = 0;

        // log users back in

        authTokens.clear();
        try {
            for (var userData : users) {
                authTokens.add(userService.login(userData).authToken());
                assertUserDAOsize(expectedUserDAOsize);
                assertAuthDAOsize(++expectedAuthDAOsize);
            }
        } catch (UserNotFoundException | MissingAttributeException | PasswordIncorrectException | SqlException e) {
            throw new RuntimeException(e);
        }

        // log users out again

        try {
            for (var authToken : authTokens) {
                userService.logout(authToken);
                assertUserDAOsize(expectedUserDAOsize);
                assertAuthDAOsize(--expectedAuthDAOsize);
            }
        } catch (AuthTokenNotFoundException | SqlException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("auth token not found")
    public void logoutUnauthorized() {

        // populate database
        try {
            registerAndLogout(new HashSet<>(), new HashSet<>());
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }

        // logout with bogus auth token

        boolean exceptionThrown = false;

        try {
            userService.logout("-1");
        } catch (AuthTokenNotFoundException e) {
            exceptionThrown = true;
        } catch (SqlException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(exceptionThrown);
    }
}
