package server.handlers.wshandling;

public class GameHasNoConnectionsException extends Exception {
    public GameHasNoConnectionsException(int gameID) {
        super("This game has no sessions: " + gameID);
    }
}
