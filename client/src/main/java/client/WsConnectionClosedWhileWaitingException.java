package client;

public class WsConnectionClosedWhileWaitingException extends Exception {
    public WsConnectionClosedWhileWaitingException(String message) {
        super(message);
    }
    public WsConnectionClosedWhileWaitingException() { super("ws connection closed while waiting for response."); }
}
