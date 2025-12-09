package server.handlers;

import org.eclipse.jetty.websocket.api.Session; // this is different from jakarta.websocket.Session?

public record UsernameAndSession(String username, Session session) {}
