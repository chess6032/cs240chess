package chess;

import chess.ChessPiece.PieceType;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private class Row {
        private final ChessPiece[] squares; // TODO: this might not be final actually

        Row() {
            squares = new ChessPiece[8];
            // TODO: do I have to initialize every element as null?
        }

        public ChessPiece getPieceAtColumn(int column) {
            return squares[column];
        }

        public void setPieceAtColumn(int column, ChessPiece piece) {
            squares[column] = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
        }

        public void setPieceAtColumn(int column, ChessGame.TeamColor team, PieceType type) {
            squares[column] = new ChessPiece(team, type);
        }

        @Override
        public String toString() {
            return "Row{" +
                    "squares=" + Arrays.toString(squares) +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass())
                return false;

            Row that = (Row) o;
            return Arrays.equals(squares, that.squares);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(squares);
        }
    }

    private final Row[] rows;

    public ChessBoard() {
        rows = new Row[8]; // initializes array of null Row references
        // fill with empty rows:
        for (int i = 0; i < rows.length; ++i) {
            rows[i] = new Row();
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        rows[position.getRow()-1].setPieceAtColumn(position.getColumn()-1, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return rows[position.getRow()-1].getPieceAtColumn(position.getColumn()-1);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // fill rows array w/ empty Rows:
        for (int i = 0; i < rows.length; ++i) {
            rows[i] = new Row();
        }

        PieceType[] backRow = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP,
                                PieceType.QUEEN, PieceType.KING,
                                PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};

        for (int i = 0; i < 8; ++i) {
            // top & bottom row:
            rows[0].setPieceAtColumn(i, ChessGame.TeamColor.WHITE, backRow[i]);
            rows[rows.length-1].setPieceAtColumn(i, ChessGame.TeamColor.BLACK, backRow[i]);

            // pawn rows:
            rows[1].setPieceAtColumn(i, ChessGame.TeamColor.WHITE, PieceType.PAWN);
            rows[rows.length-2].setPieceAtColumn(i, ChessGame.TeamColor.BLACK, PieceType.PAWN);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows.length; ++i) {
            sb.append(rows[i].toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Arrays.equals(rows, that.rows);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(rows);
    }
}
