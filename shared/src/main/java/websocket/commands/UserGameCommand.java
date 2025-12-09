package websocket.commands;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public abstract class UserGameCommand {

    private final CommandType commandType;
    private final String authToken;
    private final Integer gameID;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType getCommandType() {
        return commandType;
    }
    public String getAuthToken() {
        return authToken;
    }
    public Integer getGameID() {
        return gameID;
    }

    public static class UserGameCommandAdapter implements JsonDeserializer<UserGameCommand> {
        @Override
        public UserGameCommand deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) {
            if (!el.isJsonObject()) {
                return null;
            }

            String commandType = el.getAsJsonObject().get("commandType").getAsString();
            return switch (commandType) {
                case "CONNECT" -> ctx.deserialize(el, ConnectCommand.class);
                case "MAKE_MOVE" -> ctx.deserialize(el, MakeMoveCommand.class);
                case "LEAVE" -> ctx.deserialize(el, LeaveCommand.class);
                case "RESIGN" -> ctx.deserialize(el, ResignCommand.class);
                default -> null;
            };
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand that)) {
            return false;
        }
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }

    @Override
    public String toString() {
        return "UserGameCommand{" +
                "commandType=" + commandType +
                ", authToken='" + authToken + '\'' +
                ", gameID=" + gameID +
                '}';
    }
}
