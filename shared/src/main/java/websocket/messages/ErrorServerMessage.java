package websocket.messages;

import java.util.Objects;

public class ErrorServerMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorServerMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = "Error: " + errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ErrorServerMessage that = (ErrorServerMessage) o;
        return Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), errorMessage);
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
