package server;

import java.util.HashMap;
import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?

public class WsConnectionManager {
    // TODO: what do I store in the map??
    private final HashMap<Session, Object> connections = new HashMap<>();

    // ummmmm.....
    
}
