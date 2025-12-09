package model;

public record NotificationInfo(String username, chess.ChessGame.TeamColor team, chess.ChessMove move) {

    @Override
    public String toString() {
        return "NotificationInfo{" +
                "username='" + username + '\'' +
                ", team=" + team +
                ", move=" + move +
                '}';
    }
}
