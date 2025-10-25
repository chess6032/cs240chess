package service;

import chess.model.http.LoginRequest;
import chess.model.http.RegisterRequest;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.LoginFailException;
import org.junit.jupiter.api.*;

public class LoginServiceTests extends ServiceTests {

    @Test
    @DisplayName("login: user not in db")
    public void loginUserNotInDB() {
        boolean exceptionThrown = false;
        var request = new LoginRequest("username", "password");
        try {
            UserService.login(request, server.getUserDAO(), server.getAuthDAO());
        } catch (Exception e) {
            exceptionThrown = true;
            Assertions.assertEquals(LoginFailException.class, e.getClass());
            Assertions.assertEquals(0, server.getUserDAO().size());
            Assertions.assertEquals(0, server.getAuthDAO().size());
        }
        Assertions.assertTrue(exceptionThrown);
    }

    @Test
    @DisplayName("login: incorrect password")
    public void loginIncorrectPassword() {
        boolean exceptionThrown = false;

        // register user
        try {
            UserService.register(new RegisterRequest("username", "password", "email"), server.getUserDAO(), server.getAuthDAO());
        } catch (BadRequestException | AlreadyTakenException e) {
            throw new RuntimeException(e);
        }

        // log in with incorrect password
        try {
            UserService.login(new LoginRequest("username", "not password"), server.getUserDAO(), server.getAuthDAO());
        } catch (Exception e) {
            exceptionThrown = true;
            Assertions.assertEquals(LoginFailException.class, e.getClass());
        }
        Assertions.assertTrue(exceptionThrown);
    }

    @Test
    @DisplayName("login: user already has corresponding auth token")
    public void loginUserAlreadySignedIn() {
        boolean exceptionThrown = false;

        // register user
        try {
            UserService.register(new RegisterRequest("username", "password", "email"), server.getUserDAO(), server.getAuthDAO());
        } catch (BadRequestException | AlreadyTakenException e) {
            throw new RuntimeException(e);
        }

            int authSize = server.getAuthDAO().size();
            int userSize = server.getUserDAO().size();


        // log in user (without logging out)
        LoginRequest login = new LoginRequest("username", "password");
        try {
            UserService.login(login, server.getUserDAO(), server.getAuthDAO());
        } catch (Exception e) {
            exceptionThrown = true;
        }
        Assertions.assertFalse(exceptionThrown);
        Assertions.assertTrue(server.getAuthDAO().hasUser("username"));
        Assertions.assertEquals(authSize, server.getAuthDAO().size());
        Assertions.assertEquals(userSize, server.getUserDAO().size());
    }
}
