package websocket.messages;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public abstract class ServerMessage {
    ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public static class ServerMessageTypeAdapter implements JsonSerializer<ServerMessage>, JsonDeserializer<ServerMessage> {
        @Override
        public JsonElement serialize(ServerMessage serverMessage, Type type, JsonSerializationContext ctx) {
            JsonObject result = new JsonObject();

            // type discriminator
            result.addProperty("serverMessageType", serverMessage.getServerMessageType().name());

            // serialize actual object, with all its fields
            JsonElement serialized = ctx.serialize(serverMessage, serverMessage.getClass());

            // merge serialized fields into result
            if (serialized.isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : serialized.getAsJsonObject().entrySet()) {
                    result.add(entry.getKey(), entry.getValue());
                }
            }

            return result;
        }

        @Override
        public ServerMessage deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) {
            if (!el.isJsonObject()) {
                return null;
            }

            String msgType = el.getAsJsonObject().get("serverMessageType").getAsString();
            return switch (msgType) {
                case "LOAD_GAME" -> ctx.deserialize(el, LoadMessage.class);
                case "ERROR" -> ctx.deserialize(el, ErrorServerMessage.class);
                case "NOTIFICATION" -> ctx.deserialize(el, NotificationMessage.class);
                default -> null;
            };
        }
    }

    public static Gson buildServerMessageGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ServerMessage.class, new ServerMessageTypeAdapter())
                .create();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    @Override
    public String toString() {
        return "ServerMessage{" +
                "serverMessageType=" + serverMessageType +
                '}';
    }
}
