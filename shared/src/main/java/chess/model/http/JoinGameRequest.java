package chess.model.http;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {}
