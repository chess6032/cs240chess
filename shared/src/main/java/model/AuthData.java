package model;
import java.util.UUID;

public record AuthData(String authToken, String username) {
    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData(String username) {
        this(generateAuthToken(), username);
    }
}