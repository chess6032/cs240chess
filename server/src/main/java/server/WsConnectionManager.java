package server;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?
import org.glassfish.grizzly.utils.ArraySet;
import server.handlers.UsernameAndSession;

public class WsConnectionManager {
    private final ConcurrentHashMap<Integer, Set<UsernameAndSession>> connections = new ConcurrentHashMap<>();
    // FIXME: can one session be connected to multiple game IDs?

    public void saveSession(int gameID, UsernameAndSession userAndSesh) throws SessionSaveFailException {
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new ArraySet<>(UsernameAndSession.class));
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

//    public int gameSessionIsActiveIn(Session session) {
//        for (var gameID : connections.keySet()) {
//            var connectionsToGame = connections.get(gameID);
//            if (connectionsToGame.contains(session)) {
//                return gameID;
//            }
//        }
//        return -1; // session isn't playing any games (or smth idrk)
//    }

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

    public Session getSessionOfUser(int gameID, String username) {
        if (!connections.containsKey(gameID)) {
            return null;
        }

        var usernameAndSessions = connections.get(gameID);
        for (var userAndSesh : usernameAndSessions) {
            if (username.equals(userAndSesh.username())) {
                return userAndSesh.session();
            }
        }
        return null;
    }

}
