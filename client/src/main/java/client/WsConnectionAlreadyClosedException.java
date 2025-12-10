package client;

public class WsConnectionAlreadyClosedException extends Exception {
    public WsConnectionAlreadyClosedException(String message) {
        super(message);
    }
    public WsConnectionAlreadyClosedException() { super("tried to send message but ws connection is closed."); }
}
