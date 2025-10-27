package service;

import chess.model.UserData;

import dataaccess.exceptions.*;
import org.junit.jupiter.api.*;

public class LoginServiceTests extends ServiceTests {

    // TODO: add more tests when logout implemented

    @Test
    @DisplayName("successful")
    public void loginSuccessful() {
        UserData mario = new UserData("mario", "password", "email");
        UserData luigi = new UserData("luigi", "password", "email");

        // register users
        String marioAuthToken;
        String luigiAuthToken;
        try {
            marioAuthToken = userService.register(mario).authToken();
            luigiAuthToken = userService.register(luigi).authToken();
        } catch (AlreadyTakenException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        assertUserDAOsize(2);
        assertAuthDAOsize(2);
        Assertions.assertNotEquals(marioAuthToken, luigiAuthToken);

        String marioAuthToken2;
        String luigiAuthToken2;

        // log in user
        try {

            marioAuthToken2 = userService.login(mario).authToken();
            luigiAuthToken2 = userService.login(luigi).authToken();
            // ^ logging in a user who already has an auth token should give them
            // a NEW auth token (WITHOUT deleting the old one)
        } catch (UserNotFoundException | PasswordIncorrectException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertNotEquals(marioAuthToken, marioAuthToken2);
        Assertions.assertNotEquals(luigiAuthToken, luigiAuthToken2);

        assertUserDAOsize(2);
//        assertAuthDAOsize(3); FIXME: fix this line
    }

    @Test
    @DisplayName("unique auth token when logging in the same user")
    public void loginUniqueAuthEachTime() {
        UserData mario = new UserData("mario", "password", "email");

        // register mario

        try {
            userService.register(mario);
        } catch (AlreadyTakenException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        String authToken1;

        // log in mario

        try {
            authToken1 = userService.login(mario).authToken();
        } catch (UserNotFoundException | PasswordIncorrectException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        // log in mario again

        String authToken2;

        try {
            authToken2 = userService.login(mario).authToken();
        } catch (UserNotFoundException | PasswordIncorrectException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        // just for good measure, log in mario AGAIN

        String authToken3;

        try {
            authToken3 = userService.login(mario).authToken();
        } catch (UserNotFoundException | PasswordIncorrectException | MissingAttributeException e) {
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

        // FIXME: fix these assertions
//        Assertions.assertTrue(exception1);
//        Assertions.assertTrue(exception2);

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
        boolean exceptionThrown = isExceptionThrown(mario, luigi);

        Assertions.assertTrue(exceptionThrown);

        // register Luigi

        try {
            userService.register(luigi);
        } catch (AlreadyTakenException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        // log in Luigi (successfully)

        try {
            userService.login(luigi);
        } catch (UserNotFoundException | PasswordIncorrectException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isExceptionThrown(UserData mario, UserData luigi) {
        boolean exceptionThrown = false;

        // register Mario

        try {
            userService.register(mario);
        } catch (AlreadyTakenException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        // log in Mario (successfully)

        try {
            userService.login(mario);
        } catch (UserNotFoundException | PasswordIncorrectException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        // log in Luigi (without registering)

        try {
            userService.login(luigi);
        } catch (UserNotFoundException e) {
            exceptionThrown = true;
        } catch (PasswordIncorrectException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }
        return exceptionThrown;
    }

    @Test
    @DisplayName("password incorrect")
    public void loginPasswordIncorrect() {
        UserData mario = new UserData("mario", "password", "email");
        boolean exceptionThrown = false;

        // register Mario

        try {
            userService.register(mario);
        } catch (AlreadyTakenException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

        // log in Mario (unsuccessfully)

        try {
            userService.login(new UserData("mario", "incorrect", "email"));
        } catch (UserNotFoundException | MissingAttributeException e) {
            throw new RuntimeException(e);
        } catch (PasswordIncorrectException e) {
            exceptionThrown = true;
        }
        Assertions.assertTrue(exceptionThrown);

        // log in Mario (successfully)

        try {
            userService.login(new UserData("mario", "password", "email"));
        } catch (UserNotFoundException | PasswordIncorrectException | MissingAttributeException e) {
            throw new RuntimeException(e);
        }

    }
}
