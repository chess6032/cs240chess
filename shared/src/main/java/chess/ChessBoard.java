package chess;

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
            return squares[column - 1];
        }

        public void setPieceAtColumn(int column, ChessPiece piece) {
            squares[column - 1] = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
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

            Row row = (Row) o;
            return Objects.deepEquals(squares, row.squares);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(squares);
        }
    }

    private final Row[] rows;

    public ChessBoard() {
        rows = new Row[8]; // initializes array of null Row references
        // fill rows array w/ empty Rows:
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }
}
