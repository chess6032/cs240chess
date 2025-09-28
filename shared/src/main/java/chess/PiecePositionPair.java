package chess;

public record PiecePositionPair(ChessPiece piece, ChessPosition position) {
    public boolean isNull() {
        return piece == null || position == null;
    }

    @Override
    public String toString() {
        return "{" +
                piece +
                " at " + position +
                '}';
    }
}
