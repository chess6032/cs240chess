package service;

import chess.model.UserData;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.PasswordIncorrectException;
import dataaccess.exceptions.UserNotFoundException;
import org.junit.jupiter.api.*;

public class LoginServiceTests extends ServiceTests {

    // TODO: add more tests when logout implemented

    @Test
    @DisplayName("successful")
    public void loginSuccessful() {
        UserData mario = new UserData("mario", "password", "email");

        // register user
        String authToken;
        try {
            authToken = userService.register(mario).authToken();
        } catch (AlreadyTakenException e) {
            throw new RuntimeException(e);
        }

        assertUserDAOsize(1);
        assertAuthDAOsize(1);

        // log in user
        try {
            Assertions.assertNotEquals(authToken, userService.login(mario).authToken());
            // ^ logging in a user who already has an auth token should give them
            // the same auth token (I think)
        } catch (UserNotFoundException | PasswordIncorrectException e) {
            throw new RuntimeException(e);
        }

        assertUserDAOsize(1);
        assertAuthDAOsize(1);
    }

    @Test
    @DisplayName("unique auth token each login")
    public void loginUniqueAuthEachTime() {
        UserData mario = new UserData("mario", "password", "email");

        // register mario

        try {
            userService.register(mario);
        } catch (AlreadyTakenException e) {
            throw new RuntimeException(e);
        }

        String authToken1;

        // log in mario

        try {
            authToken1 = userService.login(mario).authToken();
        } catch (UserNotFoundException | PasswordIncorrectException e) {
            throw new RuntimeException(e);
        }

        // log in mario again

        String authToken2;

        try {
            authToken2 = userService.login(mario).authToken();
        } catch (UserNotFoundException | PasswordIncorrectException e) {
            throw new RuntimeException(e);
        }

        // just for good measure, log in mario AGAIN

        String authToken3;

        try {
            authToken3 = userService.login(mario).authToken();
        } catch (UserNotFoundException | PasswordIncorrectException e) {
            throw new RuntimeException(e);
        }

        // make sure all the auth tokens are unique

        Assertions.assertNotEquals(authToken1, authToken2);
        Assertions.assertNotEquals(authToken2, authToken3);
        Assertions.assertNotEquals(authToken1, authToken3);

        // make sure only the last token is in the db

        boolean exception1 = false;
        boolean exception2 = false;

        try {
            userService.logout(authToken1);
        } catch (AuthTokenNotFoundException e) {
            exception1 = true;
        }

        try {
            userService.logout(authToken2);
        } catch (AuthTokenNotFoundException e) {
            exception2 = true;
        }

        Assertions.assertTrue(exception1);
        Assertions.assertTrue(exception2);

        try {
            userService.logout(authToken3);
        } catch (AuthTokenNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("user not found")
    public void loginUserNotFound() {
        UserData mario = new UserData("mario", "password", "email");
        UserData luigi = new UserData("luigi", "password", "email");
        boolean exceptionThrown = false;

        // register Mario

        try {
            userService.register(mario);
        } catch (AlreadyTakenException e) {
            throw new RuntimeException(e);
        }

        // log in Mario (successfully)

        try {
            userService.login(mario);
        } catch (UserNotFoundException | PasswordIncorrectException e) {
            throw new RuntimeException(e);
        }

        // log in Luigi (without registering)

        try {
            userService.login(luigi);
        } catch (UserNotFoundException e) {
            exceptionThrown = true;
        } catch (PasswordIncorrectException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(exceptionThrown);

        // register Luigi

        try {
            userService.register(luigi);
        } catch (AlreadyTakenException e) {
            throw new RuntimeException(e);
        }

        // log in Luigi (successfully)

        try {
            userService.login(luigi);
        } catch (UserNotFoundException | PasswordIncorrectException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("password incorrect")
    public void loginPasswordIncorrect() {
        UserData mario = new UserData("mario", "password", "email");
        boolean exceptionThrown = false;

        // register Mario

        try {
            userService.register(mario);
        } catch (AlreadyTakenException e) {
            throw new RuntimeException(e);
        }

        // log in Mario (unsuccessfully)

        try {
            userService.login(new UserData("mario", "incorrect", "email"));
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        } catch (PasswordIncorrectException e) {
            exceptionThrown = true;
        }
        Assertions.assertTrue(exceptionThrown);

        // log in Mario (successfully)

        try {
            userService.login(new UserData("mario", "password", "email"));
        } catch (UserNotFoundException | PasswordIncorrectException e) {
            throw new RuntimeException(e);
        }

    }
}
