package server;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?
//import org.glassfish.grizzly.utils.ArraySet;
import server.handlers.wshandling.UsernameAndSession;

public class WsConnectionManager {
    private final ConcurrentHashMap<Integer, Set<UsernameAndSession>> connections = new ConcurrentHashMap<>();
    // FIXME: can one session be connected to multiple game IDs?

    public void saveSession(int gameID, UsernameAndSession userAndSesh) throws SessionSaveFailException {
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new HashSet<>());
        }
        var sessions = connections.get(gameID);

        if (sessions.contains(userAndSesh)) {
            throw new SessionSaveFailException("Session already connected to " + gameID);
        }
        if (!sessions.add(userAndSesh)) {
            throw new SessionSaveFailException("idek what went wrong here can't lie: " + gameID);
        }
    }

    public boolean removeSession(int gameID, UsernameAndSession userAndSesh) {
        if (!connections.containsKey(gameID)) {
            // game doesn't exist, or no sessions are playing/observing that game
            return false;
        }

        var sessions = connections.get(gameID);
        return sessions.remove(userAndSesh);
    }

    public boolean sessionIsInThisGame(UsernameAndSession userAndSesh, int gameID) {
        var connectionsToGame = connections.get(gameID);
        if (connectionsToGame == null) {
            return false;
        }
        return connectionsToGame.contains(userAndSesh);
    }

    public Collection<UsernameAndSession> getSessionsInGameID(int gameID) {
        return connections.get(gameID);
    }
}
