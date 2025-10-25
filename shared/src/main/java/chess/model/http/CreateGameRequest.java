package chess.model.http;

public record CreateGameRequest(String authToken, String gameName) {}
