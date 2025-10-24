package service;

import dataaccess.BadRequestException;
import dataaccess.MemoryDAO.MemoryAuthDAO;
import dataaccess.MemoryDAO.MemoryUserDAO;
import server.Server;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import java.security.Provider;

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
        }

        Assertions.assertEquals(users_size, users.size());
        Assertions.assertEquals(auths_size, auths.size());
    }

    @Test
    @DisplayName("register: successful")
    public void registerSuccessful() {

    }
}
