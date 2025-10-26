package service;

import chess.model.UserData;

import dataaccess.exceptions.AlreadyTakenException;
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
            Assertions.assertEquals(authToken, userService.login(mario).authToken());
            // ^ logging in a user who already has an auth token should give them
            // the same auth token (I think)
        } catch (UserNotFoundException | PasswordIncorrectException e) {
            throw new RuntimeException(e);
        }

        assertUserDAOsize(1);
        assertAuthDAOsize(1);
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
