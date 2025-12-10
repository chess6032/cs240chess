package client;

public class WsTimeoutException extends Exception {
    public WsTimeoutException(String message) {
        super(message);
    }

    public WsTimeoutException() {
        super("server timed out");
    }
}
