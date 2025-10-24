package service;

import chess.model.RegisterRequest;
import server.CommonExceptions;
import server.ErrorMessage;
import server.Server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

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
        sendRequestAndGetResponse("/db", null);
    }

    // UTILITY METHODS AND CLASSES

    private HttpResponse<String> sendRequestAndGetResponse(String path, Object body) {
        String endpointURL = serverURL + path;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointURL))
                .header("Content-Type", "application/json") // metadata: says body is JSON
                .POST(HttpRequest.BodyPublishers.ofString(serializer.toJson(body))) // body
//                .timeout(java.time.Duration.ofSeconds(10)) // (optional) specifies timeout
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    private void assertErrorResponseEquals(int statusCode, String message, HttpResponse<String> response) {
        // assert status code is correct
        Assertions.assertEquals(statusCode, response.statusCode());
        // assert error message is correct
        Assertions.assertEquals(message, new Gson().fromJson(response.body(), ErrorMessage.class).message());
    }

    private record bogusInput(String str, int num) {}

    // TESTS

    @Test
    @DisplayName("clear application")
    public void clear() {
        var response = sendRequestAndGetResponse("/db", null);
        Assertions.assertEquals(CommonExceptions.SUCCESS_STATUS, response.statusCode());
        // TODO: Once HTTP GET requests are implemented, make sure that the databases are actually empty.
    }

    @Test
    @DisplayName("register: bad input - completely wrong object")
    public void registerBadInput1() {
        var response = sendRequestAndGetResponse("/user", new bogusInput("skibidi", 67));
        assertErrorResponseEquals(CommonExceptions.BAD_REQUEST_STATUS, CommonExceptions.BAD_REQUEST_MSG, response);
    }

    @Test
    @DisplayName("register: bad input - empty username")
    public void registerBadInput2() {
        RegisterRequest emptyUsernameRequest = new RegisterRequest("", "password", "email");
        var response = sendRequestAndGetResponse("/user", emptyUsernameRequest);
        assertErrorResponseEquals(CommonExceptions.BAD_REQUEST_STATUS, CommonExceptions.BAD_REQUEST_MSG, response);
    }

    @Test
    @DisplayName("register: bad input - empty password")
    public void registerBadInput3() {
        RegisterRequest emptyPasswordRequest = new RegisterRequest("username", "", "email");
        var response = sendRequestAndGetResponse("/user", emptyPasswordRequest);
        assertErrorResponseEquals(CommonExceptions.BAD_REQUEST_STATUS, CommonExceptions.BAD_REQUEST_MSG, response);
    }

    @Test
    @DisplayName("register: username already taken")
    public void registerAlreadyTaken() {
        var req = new RegisterRequest("username", "password", "email");
        sendRequestAndGetResponse("/user", req);
        var response = sendRequestAndGetResponse("/user", req);
        assertErrorResponseEquals(CommonExceptions.ALREADY_TAKEN_STATUS, CommonExceptions.ALREADY_TAKEN_MSG, response);
    }

    @Test
    @DisplayName("register: empty input")
    public void registerEmptyInput() {
        var response = sendRequestAndGetResponse("/user", new JsonObject());
        assertErrorResponseEquals(CommonExceptions.BAD_REQUEST_STATUS, CommonExceptions.BAD_REQUEST_MSG, response);
    }

    @Test
    @DisplayName("register: successful")
    public void registerSuccessful() {
        var requests = new ArrayList<RegisterRequest>();
        requests.add(new RegisterRequest("username", "password", "email"));
        requests.add(new RegisterRequest("username", "password", "email"));
        requests.add(new RegisterRequest("username", "password", "email"));
    }
}
