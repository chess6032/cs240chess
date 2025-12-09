package server;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?
import org.glassfish.grizzly.utils.ArraySet;
import server.handlers.GameHasNoConnectionsException;

public class WsConnectionManager {
    private final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();
    // FIXME: can one session be connected to multiple game IDs?

    public void saveSession(int gameID, Session session) throws SessionSaveFailException {
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new ArraySet<>(Session.class));
        }
        var sessions = connections.get(gameID);
        if (sessions.contains(session)) {
            throw new SessionSaveFailException("Session already connected to " + gameID);
        }
        if (!sessions.add(session)) {
            throw new SessionSaveFailException("idek what went wrong here can't lie: " + gameID);
        }
    }

    public boolean removeSession(int gameID, Session session) {
        if (!connections.containsKey(gameID)) {
            // game doesn't exist, or no sessions are playing/observing that game
            return false;
        }

        var sessions = connections.get(gameID);
        return sessions.remove(session);
    }

    public int gameSessionIsActiveIn(Session session) {
        for (var gameID : connections.keySet()) {
            var connectionsToGame = connections.get(gameID);
            if (connectionsToGame.contains(session)) {
                return gameID;
            }
        }
        return -1; // session isn't playing any games (or smth idrk)
    }

    public boolean sessionIsInThisGame(Session session, int gameID) {
        var connectionsToGame = connections.get(gameID);
        if (connectionsToGame == null) {
            return false;
        }
        return connectionsToGame.contains(session);
    }

    public Collection<Session> sessionsInGameID(int gameID) {
        return connections.get(gameID);
    }

}
