package server;

import java.util.HashSet;

import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?

public class WsConnectionManager {
    // TODO: what do I store in the map??
    private final HashSet<Session> connections = new HashSet<>();

    // ummmmm.....

}
