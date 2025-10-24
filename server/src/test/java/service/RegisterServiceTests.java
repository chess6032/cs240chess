package service;

import chess.model.http.RegisterRequest;
import dataaccess.BadRequestException;
import dataaccess.AlreadyTakenException;

import org.junit.jupiter.api.*;

import java.util.HashSet;

public class RegisterServiceTests extends ServiceTests {

    @Test
    @DisplayName("register: bad input - empty username")
    public void registerEmptyUsername() {
        var request = new RegisterRequest("", "password", "email");

        try {
            UserService.register(request, server.getUserDAO(), server.getAuthDAO());
        } catch (Exception e) {
            Assertions.assertEquals(BadRequestException.class, e.getClass());
            printMsg(e);
        }
    }

    @Test
    @DisplayName("register: bad input - empty password")
    public void registerEmptyPassword() {
        var request = new RegisterRequest("username", "", "email");

        try {
            UserService.register(request, server.getUserDAO(), server.getAuthDAO());
        } catch (Exception e) {
            Assertions.assertEquals(BadRequestException.class, e.getClass());
            printMsg(e);
        }
    }

    @Test
    @DisplayName("register: username already taken")
    public void registerAlreadyTaken() {
        var user1 = new RegisterRequest("username", "password", "email");
        var user2 = new RegisterRequest("username", "differentPswrd", "differentEmail");

        // add user
        try {
            UserService.register(user1, server.getUserDAO(), server.getAuthDAO());
        } catch (AlreadyTakenException | BadRequestException e) {
            throw new RuntimeException(e);
        }

        // add user again
        try {
            UserService.register(user2, server.getUserDAO(), server.getAuthDAO());
        } catch (Exception e) {
            // assert the correct exception was thrown
            Assertions.assertEquals(AlreadyTakenException.class, e.getClass());
            printMsg(e);
        }
    }

    @Test
    @DisplayName("register: empty input")
    public void registerEmptyInput() {
        var users = server.getUserDAO();
        int users_size = users.size();

        var auths = server.getAuthDAO();
        int auths_size = auths.size();

        boolean exceptionThrown = false;

        try {
            UserService.register(null, users, auths);
        } catch (Exception e) {
            exceptionThrown = true;
            // assert the correct exception was thrown
            Assertions.assertEquals(BadRequestException.class, e.getClass());
            // print exception's message
            printMsg(e);
        }

        Assertions.assertTrue(exceptionThrown);

        Assertions.assertEquals(users_size, users.size());
        Assertions.assertEquals(auths_size, auths.size());
    }

    @Test
    @DisplayName("register: successful")
    public void registerSuccessful() {
        HashSet<RegisterRequest> requests = new HashSet<>();
        requests.add(new RegisterRequest("username", "password", "email"));
        requests.add(new RegisterRequest("caleb", "password", "email"));
        requests.add(new RegisterRequest("hessing", "password", "email"));
        requests.add(new RegisterRequest("skibidi", "password", "email"));
        requests.add(new RegisterRequest("skibidi2", "ohio rizz", ""));

        for (var request : requests) {
            try {
                UserService.register(request, server.getUserDAO(), server.getAuthDAO());
            } catch (AlreadyTakenException | BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        Assertions.assertEquals(requests.size(), server.getUserDAO().size());
        Assertions.assertEquals(requests.size(), server.getAuthDAO().size());
    }
}
