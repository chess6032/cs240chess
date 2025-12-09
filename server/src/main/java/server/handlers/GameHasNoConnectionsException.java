package server.handlers;

import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?

public class GameHasNoConnectionsException extends Exception {
    public GameHasNoConnectionsException(int gameID) {
        super("This game has no sessions: " + gameID);
    }
}
