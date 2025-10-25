package service;

import chess.model.http.LogoutRequest;
import chess.model.http.RegisterRequest;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.BadRequestException;
import org.junit.jupiter.api.*;

public class LogoutServiceTests extends ServiceTests {
    @Test
    @DisplayName("logout: successful")
    public void logout() {

        // register user
        try {
            UserService.register(new RegisterRequest("username", "password", "email"), server.getUserDAO(), server.getAuthDAO());
        } catch (BadRequestException | AlreadyTakenException e) {
            throw new RuntimeException(e);
        }

        String authTkn = server.getAuthDAO().getAuthTkn("username");

        int authSize = server.getAuthDAO().size();
        int userSize = server.getUserDAO().size();

        try {
            UserService.logout(new LogoutRequest(authTkn), server.getUserDAO(), server.getAuthDAO());
        } catch (AuthTokenNotFoundException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(authSize - 1, server.getAuthDAO().size());
        Assertions.assertEquals(userSize - 1, server.getUserDAO().size());
        Assertions.assertNull(server.getAuthDAO().getUsername(authTkn));
        Assertions.assertNull(server.getAuthDAO().getAuthTkn("username"));
        Assertions.assertNull(server.getUserDAO().getUser("username"));
    }
}
