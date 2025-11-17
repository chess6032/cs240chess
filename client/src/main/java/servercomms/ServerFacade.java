package servercomms;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.PlayerColorGameIDforJSON;
import model.UserData;
import org.junit.jupiter.api.Assertions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collection;

public class ServerFacade {
    private final Gson serializer = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }

    // HTTP UTILITY METHODS

    private static String neverEndWithSlash(String path) {
        if (!(path == null || path.isEmpty())) {
            if (path.charAt(path.length() - 1) == '/') {
                return path.substring(0, path.length() - 1);
            }
        }

        return path;
    }

    private static String alwaysBeginWithSlash(String path) {
        if (!(path == null || path.isEmpty())) {
            if (path.charAt(0) != '/') {
                return "/" + path;
            }
        }

        return path;
    }

    private HttpRequest buildRequest(String method, String path, Object body, AuthData auth) {

        String fullPath = neverEndWithSlash(serverURL) + alwaysBeginWithSlash(path);
        var request = HttpRequest.newBuilder()
                .uri(URI.create(fullPath))
                .method(method, makeRequestBody(body));

        if (auth != null && auth.authToken() != null) {
            request.setHeader("Authorization", auth.authToken());
        }

        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        // serialize request, then sends it in Java's BodyPublisher class
        if (request == null) {
            return BodyPublishers.noBody();
        }
        return BodyPublishers.ofString(new Gson().toJson(request));
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2; // check if the status is in the 200s
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();

        // throw error if status code indicates request wasn't successful
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        // if successful, return body
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        // if successful, return nothing if the body is empty
        return null;
    }

    private <T> T buildSendHandle(String method, String path, Object body, AuthData authHeader, Class<T> returnClass) throws ResponseException {
        var request = buildRequest(method, path, body, authHeader);
        var response = sendRequest(request);
        return handleResponse(response, returnClass);
    }

    // SERVER COMMUNICATION METHODS

    public void clear() throws ResponseException {
        // TODO: delete this probably but idk maybe it'll be nice to have
        buildSendHandle("DELETE", "/db", null, null,
                null);
    }

    public AuthData register(UserData user) throws ResponseException {
        return buildSendHandle("POST", "/user", user, null,
                AuthData.class);
    }

    public AuthData login(UserData user) throws ResponseException {
        return buildSendHandle("POST", "/session", user, null,
                AuthData.class);
    }

    public void logout(AuthData auth) throws ResponseException {
        buildSendHandle("DELETE", "/session", null, auth,
                null);
    }

    public GameData createGame(AuthData auth, String gameName) throws ResponseException {
        GameData gameNameForSerialization = new GameData(-1, null, null, gameName, null);
        return buildSendHandle("POST", "/game", gameNameForSerialization, auth,
                GameData.class);
    }

    public Collection<GameData> listGames(AuthData auth) throws ResponseException {
        return buildSendHandle("GET", "/game", null, auth,
                Collection.class); // FIXME: will this work fine with Collection.class ?
    }

    public void joinGame(AuthData auth, PlayerColorGameIDforJSON colorAndID) throws ResponseException {
        buildSendHandle("PUT", "/game", colorAndID, auth,
                null);
    }

    public static void main(String[] args) {
        String a = "a/";
        String b = "b";
        String x = "/x";
        String y = "y";

        Assertions.assertEquals("a/x", neverEndWithSlash(a) + alwaysBeginWithSlash(x));
        Assertions.assertEquals("a/y", neverEndWithSlash(a) + alwaysBeginWithSlash(y));
        Assertions.assertEquals("b/x", neverEndWithSlash(b) + alwaysBeginWithSlash(x));
        Assertions.assertEquals("b/y", neverEndWithSlash(b) + alwaysBeginWithSlash(y));
    }
}
