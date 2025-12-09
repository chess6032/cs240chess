package websocket.messages;

import model.NotificationInfo;

import java.util.Objects;

public class NotificationMessage extends ServerMessage {

    public enum NotificationType {
        PLAYER_JOINED,
        OBSERVER_JOINED,
        PLAYER_MADE_MOVE,
        PLAYER_LEFT,
        PLAYER_RESIGNED,
        PLAYER_IN_CHECK,
        PLAYER_IN_CHECKMATE
    }

    private final NotificationInfo info;
    private final NotificationType type;

    private final String message; // the passoff tests require that NotificationMessage have a member called "message"

    public NotificationMessage(NotificationInfo info, NotificationType type) {
        super(ServerMessageType.NOTIFICATION);
        this.info = info;
        this.type = type;
        message = toString();
    }

    public NotificationInfo getInfo() {
        return info;
    }

    public NotificationType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        NotificationMessage that = (NotificationMessage) o;
        return Objects.equals(info, that.info) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), info, type);
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "info=" + info +
                ", type=" + type +
                '}';
    }
}
