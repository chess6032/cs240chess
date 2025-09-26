package chess;

public record PieceMovePair(ChessPiece piece, ChessMove move) {
    @Override
    public String toString() {
        return "{" +
                piece +
                ": " + move +
                '}';
    }
}
