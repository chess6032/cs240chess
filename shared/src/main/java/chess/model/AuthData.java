package chess.model;

public record AuthData(String authToken, String username) {
    public AuthData(String username) {
        this.username = username;
        this.authToken = /* generate new auth token (UUID?) */;
    }
}