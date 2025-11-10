package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;

import server.Server;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ServiceTests {
    protected static Server server;

    protected static UserDAO userDAO;
    protected static AuthDAO authDAO;
    protected static GameDAO gameDAO;

    protected static UserService userService;
    protected static GameService gameService;

    // UTILITY

    protected void assertUserDAOsize(int expected) {
        try {
            Assertions.assertEquals(expected, userDAO.size());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void assertAuthDAOsize(int expected) {
        Assertions.assertEquals(expected, authDAO.size());
    }

    protected void assertGameDAOsize(int expected) {
        Assertions.assertEquals(expected, gameDAO.size());
    }

    // TESTING

    @BeforeAll
    public static void init() {
        server = new Server();

        int port = server.run(0);
        System.out.println("Start test HTTP server on " + port);

        userDAO = server.getUserDAO();
        authDAO = server.getAuthDAO();
        gameDAO = server.getGameDAO();
        userService = server.getUserService();
        gameService = server.getGameService();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
        System.out.println("Test HTTP server closed.");
    }

    @BeforeEach
    void clearApplication() {
        server.clear(null);
        assertUserDAOsize(0);
        assertAuthDAOsize(0);
        assertGameDAOsize(0);
    }

}