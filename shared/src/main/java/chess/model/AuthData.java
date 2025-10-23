package chess.model;
import java.util.UUID;

public record AuthData(String authToken, String username) {
    public AuthData(String username) {
        this(username, UUID.randomUUID().toString());
    }
}