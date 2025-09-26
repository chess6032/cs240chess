package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import java.util.Iterator;
import java.util.NoSuchElementException;

import chess.ChessPiece.PieceType;
import static chess.ChessPiece.PieceType.*;
import chess.ChessGame.TeamColor;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Iterable<PiecePositionPair>{

    private final ChessPiece[][] grid;
    private static final int BOARD_WIDTH = 8;

    private ChessPosition whiteKingPosition = null;
    private ChessPosition blackKingPosition = null;

    public static int getBoardWidth() {
        return BOARD_WIDTH;
    }

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
        if (position == null) {
            throw new RuntimeException("ChessBoard.addPiece: somehow, position is null??");
        }

        if (isPositionOutOfBounds(position)) {
            throw new RuntimeException("ChessBoard.addPiece: position out of bounds: " + position);
        }

        if (piece == null) {
            return;
//            throw new RuntimeException("ChessBoard.addPiece: inputted chess piece is null (how did this happen?)");
        }

        if (piece.getPieceType() == KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteKingPosition = new ChessPosition(position.getRow(), position.getColumn()); // TODO: do I need to do a copy like this?
            } else {
                blackKingPosition = new ChessPosition(position.getRow(), position.getColumn()); // TODO: do I need to do a copy like this?
            }
        }

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
        if (isPositionOutOfBounds(position)) {
            throw new RuntimeException("ChessPiece.getPiece: Position out of bounds: " + position.toString());
//            return new ChessPiece(null, null);
        }
        return grid[position.getRow()-1][position.getColumn()-1];
        // TODO: should I return a copy??
    }

    public static boolean isPositionOutOfBounds(ChessPosition position) {
        if (   position.getRow()    < 1 || position.getRow()    > BOARD_WIDTH
            || position.getColumn() < 1 || position.getColumn() > BOARD_WIDTH)
        {
//            System.out.println("Position IS out of bounds: " + position);
            return true;
        }
        return false;
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
                PieceType.QUEEN, KING,
            PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
    };

    @Override
    public String toString() {
        return toString((Collection<ChessPosition>) null);
    }

    public String toString(Collection<ChessPosition> positions) {
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
                ChessPosition square = new ChessPosition(row, col);
                ChessPiece piece = getPiece(square);

                String squareString = " ";
                if (positions != null && positions.contains(square)) {
                    squareString = "_";
                    if (piece != null)
                        squareString = "X";
                } else {
                    if (piece != null)
                        squareString = piece.toString();
                }
                sb.append(squareString);

//                sb.append((piece != null ? piece.toString() : " ")); // null reference means space is empty
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

    @Override
    public Iterator<PiecePositionPair> iterator() {
        return new Iterator<>() {
            private int rowIdx = 0;
            private int colIdx = 0;
            private int nextRowIdx;
            private int nextColIdx;

            private boolean findNextNonNull() {
                int r = rowIdx;
                int c = colIdx+1;

                while (r < grid.length) {
                    while (c < grid[r].length) {
                        if (grid[r][c] != null) {
                            nextRowIdx = r;
                            nextColIdx = c;
                            return true;
                        }
                        ++c;
                    }
                    ++r;
                    c = 0;
                }

//                nextRowIdx = -1;
//                nextColIdx = -1;
                return false;
            }

            /**
             * Checks if there is a next element to iterate over.
             *
             * @return true if there are more elements, false otherwise.
             */
            @Override
            public boolean hasNext() {
                return findNextNonNull();
            }

            /**
             * Returns the next element in the iteration.
             *
             * @return The next integer element.
             * @throws NoSuchElementException if there are no more elements.
             */
            @Override
            public PiecePositionPair next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                ChessPiece piece = grid[rowIdx][colIdx];
                ChessPosition position = new ChessPosition(rowIdx +1, colIdx +1);

                rowIdx = nextRowIdx;
                colIdx = nextColIdx;

                return new PiecePositionPair(piece, position);
            }
        };
    }

    public ChessPosition getKingPosition(TeamColor team) {
        ChessPosition kingPosition = team == TeamColor.WHITE ? whiteKingPosition : blackKingPosition;
        ChessPiece shouldBeKing = getPiece(kingPosition);
        if (shouldBeKing == null || shouldBeKing.getPieceType() != KING) {
            throw new RuntimeException(team + "'s king position " + kingPosition + " does not hold king, instead holds: " + shouldBeKing );
        }
        return kingPosition;
    }

}
