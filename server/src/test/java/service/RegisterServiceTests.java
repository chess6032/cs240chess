package service;

import dataaccess.BadRequestException;
import dataaccess.MemoryDAO.MemoryAuthDAO;
import dataaccess.MemoryDAO.MemoryUserDAO;
import server.Server;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegisterServiceTests {
    private static Server server;
    private static int port;
    private static final Gson serializer = new Gson();
    private static final String BASE_URL = "http://localhost:";
    private static String serverURL;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        serverURL = BASE_URL + port;
        System.out.println("Start test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
        System.out.println("Test HTTP server closed.");
    }

    @BeforeEach
    void clearApplication() {
        server.clear(null);
    }

    // TESTS

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
            Assertions.assertEquals(BadRequestException.class, e.getClass());
            Assertions.assertEquals("", e.getMessage());
        }
        Assertions.assertEquals(users_size, users.size());
        Assertions.assertEquals(auths_size, auths.size());
    }

    @Test
    @DisplayName("register: successful")
    public void registerSuccessful() {

    }
}
