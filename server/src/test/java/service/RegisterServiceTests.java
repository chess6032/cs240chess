package service;

import chess.model.UserData;
import dataaccess.BadRequestException;
import dataaccess.UsernameAlreadyTakenException;

import org.junit.jupiter.api.*;
import server.CommonExceptions;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegisterServiceTests extends ServiceTests {

    @Test
    @DisplayName("register: bad input - empty username")
    public void registerEmptyUsername() {

    }

    @Test
    @DisplayName("register: bad input - empty password")
    public void registerEmptyPassword() {

    }

    @Test
    @DisplayName("register: username already taken")
    public void registerAlreadyTaken() {
        var request = new UserData("username", "password", "email");

        // add user

        try {
            UserService.register(request, server.getUserDAO(), server.getAuthDAO());
        } catch (UsernameAlreadyTakenException | BadRequestException e) {
            throw new RuntimeException(e);
        }

        // add same user

        try {
            UserService.register(request, server.getUserDAO(), server.getAuthDAO());
        } catch (Exception e) {
            // assert the correct exception was thrown
            Assertions.assertEquals(UsernameAlreadyTakenException.class, e.getClass());
            // assert the correct message was thrown
            Assertions.assertEquals(CommonExceptions.ALREADY_TAKEN_MSG, e.getMessage());
        }

    }

    @Test
    @DisplayName("register: empty input")
    public void registerEmptyInput() {
        var users = server.getUserDAO();
        int users_size = users.size();

        var auths = server.getAuthDAO();
        int auths_size = auths.size();

        try {
            UserService.register(null, users, auths);
        } catch (Exception e) {
            // assert the correct exception was thrown
            Assertions.assertEquals(BadRequestException.class, e.getClass());
            // assert the correct message was thrown
            Assertions.assertEquals(CommonExceptions.BAD_REQUEST_MSG, e.getMessage());
        }

        Assertions.assertEquals(users_size, users.size());
        Assertions.assertEquals(auths_size, auths.size());
    }

    @Test
    @DisplayName("register: successful")
    public void registerSuccessful() {

    }
}
