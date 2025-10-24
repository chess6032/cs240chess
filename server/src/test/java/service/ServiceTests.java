package service;

import org.junit.jupiter.api.*;
import server.Server;

public class ServiceTests {
    protected static Server server;

    // UTILITY

    protected void printMsg(Exception e) {
        if (e.getMessage().isBlank()) {
            System.out.println("(exception had no message)");
            return;
        }
        System.out.println(e.getMessage());
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
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

}
