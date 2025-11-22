package client;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    private static final UserData mario = new UserData("mario", "mario64", "supermario@mariobrosplumbing.com");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost/" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterPositive() {
        try {
            serverFacade.register(mario);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        AuthData auth;

        try {
            auth = serverFacade.login(mario);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(mario.username(), auth.username());
    }

    @Test
    public void testRegisterFail() {
        try {
            serverFacade.register(mario);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        boolean exceptionThrown = false;

        try {
            serverFacade.register(mario);
        } catch (ResponseException e) {
            exceptionThrown = true;
        }

        Assertions.assertTrue(exceptionThrown);
    }
}
