package server;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?
//import org.glassfish.grizzly.utils.ArraySet;
import server.handlers.wshandling.UsernameAndSession;

public class WsConnectionManager {
    private final ConcurrentHashMap<Integer, Set<UsernameAndSession>> connections = new ConcurrentHashMap<>();
    // FIXME: can one session be connected to multiple game IDs?

    public synchronized void saveSession(int gameID, UsernameAndSession userAndSesh) throws SessionSaveFailException {

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

    public synchronized boolean removeSession(int gameID, UsernameAndSession userAndSesh) {
        if (!connections.containsKey(gameID)) {
            // game doesn't exist, or no sessions are playing/observing that game
            return false;
        }

        var sessions = connections.get(gameID);
        if (!sessions.remove(userAndSesh)) {
            return false;
        }
        userAndSesh.session().close();

        sessions.remove(userAndSesh);
        return true;
    }

    public synchronized boolean sessionIsInThisGame(UsernameAndSession userAndSesh, int gameID) {
        var connectionsToGame = connections.get(gameID);
        if (connectionsToGame == null) {
            return false;
        }
        if (!userAndSesh.session().isOpen()) {
            removeSession(gameID, userAndSesh);
            userAndSesh.session().close();
            return false;
        }
        return connectionsToGame.contains(userAndSesh);
    }

    public synchronized Collection<UsernameAndSession> getSessionsInGameID(int gameID) {
        var uASes = connections.get(gameID);
        if (uASes == null) {
            return new HashSet<>();
        }

        Iterator<UsernameAndSession> itr = uASes.iterator();
        while (itr.hasNext()) {
            var uAS = itr.next();
            if (!uAS.session().isOpen()) {
                itr.remove(); // safe removal via iterator
                uAS.session().close(); // just to be sure
            }
        }

        return uASes;
    }

}
