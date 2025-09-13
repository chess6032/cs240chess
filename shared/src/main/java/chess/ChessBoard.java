package chess;

import java.util.Arrays;
import java.util.Objects;

import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] grid;
    private static final int BOARD_WIDTH = 8;

    public ChessBoard() {
        grid = new ChessPiece[BOARD_WIDTH][BOARD_WIDTH];
        // TODO: do I have to initialize everything as null?
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        grid[position.getRow()-1][position.getColumn()-1] = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
        // TODO: could I just set it to `piece` instead?? idk how tf Java works, man.
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return grid[position.getRow()-1][position.getColumn()-1];
        // TODO: should I return a copy??
    }

    public boolean isPositionOutOfBounds(ChessPosition position) {
        return position.getRow() < 1 || position.getRow() > BOARD_WIDTH
            || position.getColumn() < 1 || position.getColumn() > BOARD_WIDTH;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i < BOARD_WIDTH; ++i) {
            // black starting pieces:
            addPiece(new ChessPosition(BOARD_WIDTH, i+1), new ChessPiece(TeamColor.BLACK, edgeRows[i])); // top row (black)
            addPiece(new ChessPosition(BOARD_WIDTH-1, i+1), new ChessPiece(TeamColor.BLACK, PieceType.PAWN)); // black pawns

            // white starting pieces:
            addPiece(new ChessPosition(1, i+1), new ChessPiece(TeamColor.WHITE, edgeRows[i])); // bottom row (white)
            addPiece(new ChessPosition(2, i+1), new ChessPiece(TeamColor.WHITE, PieceType.PAWN)); // white pawns
        }
    }

    private static final PieceType[] edgeRows = {
            // piece arrangement for top & bottom row:
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP,
                PieceType.QUEEN, PieceType.KING,
            PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
    };

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("   1 2 3 4 5 6 7 8\n"); // column key

        for (int i = 0; i < BOARD_WIDTH; ++i) {
            int row = BOARD_WIDTH - i;
            sb.append(row); // row key
            sb.append(" ");

            // convert row to string:
            for (int j = 0; j < BOARD_WIDTH; ++j) {
                int col = j+1;
                sb.append("|");
                ChessPiece piece = getPiece(new ChessPosition(row, col));
                sb.append((piece != null ? piece.toString() : " ")); // null reference means space is empty
            }

            sb.append("|");

            if (i < BOARD_WIDTH - 1)
                sb.append("\n"); // don't add newline on last row
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (this == o) {
            return true; // TODO: do I need this? (Rodham put it in his equals override, but Jensen didn't...)
        }

        ChessBoard that = (ChessBoard) o;
        return toString().equals(that.toString()); // FIXME: this is lazy but it works so...
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(grid));
    }
}
