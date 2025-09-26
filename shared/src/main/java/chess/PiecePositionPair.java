package chess;

public record PiecePositionPair(ChessPiece piece, ChessPosition position) {
    @Override
    public String toString() {
        return "{" +
                piece +
                " at " + position +
                '}';
    }
}
