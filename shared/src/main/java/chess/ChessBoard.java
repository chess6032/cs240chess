package chess;

import java.util.Arrays;

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
        grid = new ChessPiece[8][8];
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

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i < BOARD_WIDTH; ++i) {
            grid[0][i] = new ChessPiece(TeamColor.BLACK, edgeRows[i]) ; // top row (black)
            grid[BOARD_WIDTH-1][i] = new ChessPiece(TeamColor.WHITE, edgeRows[i]) ; // bottom row (white)
            grid[1][i] = new ChessPiece(TeamColor.BLACK, PieceType.PAWN); // black pawns
            grid[BOARD_WIDTH-2][i] = new ChessPiece(TeamColor.WHITE, PieceType.PAWN); // white pawns
        }
    }

    private final PieceType[] edgeRows = {
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP,
                PieceType.QUEEN, PieceType.KING,
            PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
    };

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
//        System.out.println("GRID LENGTH: " + grid.length);
        for (int i = 0; i < BOARD_WIDTH; ++i) {
//            System.out.println("grid[" + i + "].length = " + grid[i].length);
            for (int j = 0; j < BOARD_WIDTH; ++j) {
//                System.out.println("i: " + i + ", j: " + j);
                sb.append(grid[i][j]);
                sb.append(" ");
            }
            if (i < BOARD_WIDTH - 1)
                sb.append("\n");
        }
        return sb.toString();
    }
}
