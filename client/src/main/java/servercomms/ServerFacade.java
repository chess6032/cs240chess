package servercomms;

import chess.model.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }

    // SERVER COMMUNICATION METHODS

    // TODO: implement these somehow
    public void register() {

    }
    public void login() {

    }
    public void logout() {

    }
    public void createGame() {

    }
    public void listGames() {

    }
    public void joinGame() {

    }
}
