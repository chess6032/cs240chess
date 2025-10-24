package service;

import chess.model.RegisterRequest;
import server.ExceptionStatusCodes;
import server.Server;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

    @Test
    @DisplayName("register: bad input")
    public void registerBadInput() {
        String endpointURL = serverURL + "/user";
        record bogusInput(String str, int num) {}
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointURL))
                .header("Content-Type", "application/json") // metadata: says body is JSON
                .POST(HttpRequest.BodyPublishers.ofString(serializer.toJson(new bogusInput("skibidi", 67)))) // body
//                .timeout(java.time.Duration.ofSeconds(10)) // (optional) specifies timeout
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(ExceptionStatusCodes.BAD_REQUEST, response.statusCode());
    }
}
