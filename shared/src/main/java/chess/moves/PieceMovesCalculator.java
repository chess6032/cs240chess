package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class PieceMovesCalculator {

    private final ChessBoard board;
    private final ChessPosition position;
    private final TeamColor team;

    private final ChessPiece.PieceType promotionPiece; // only the subclass for the Pawn will use this.

    private final ArrayList<ChessMove> possibleMoves;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position, TeamColor team) {
        this.board = board;
        this.position = position;
        this.team = team;
        possibleMoves = new ArrayList<ChessMove>();

        promotionPiece = null; // TODO: override this for pawn.
    }

    public ArrayList<ChessMove> getPossibleMoves() {
        return possibleMoves;
        // TODO: return copy???
    }

    public ChessPosition calculateRelativePosition(int dRow, int dCol) {
        return new ChessPosition(position.getRow() + dRow, position.getColumn() + dCol);
    }

    public void calculateMoves() {
        // OVERRIDE THIS IN SUBCLASSES
        throw new RuntimeException("calculateMoves invoked on PieceMovesCalculator (the move calculator superclass). " +
                "Did you use MoveCalculatorFactory to get your PieceMovesCalculator?" +
                "(If so, check that MoveCalculatorFactory is working properly. " +
                "It should return a SUBCLASS of PieceMovesCalculator.)");
    }

    protected void addMoveIfSpaceEmpty(ChessPosition newPosition) {
        if (board.isSquareEmpty(newPosition)) {
            possibleMoves.add(new ChessMove(position, newPosition, promotionPiece));
        }
    }

    protected void addMoveIfRelativeSpaceEmpty(int dRow, int dCol) {
        addMoveIfSpaceEmpty(calculateRelativePosition(dRow, dCol));
    }
}
