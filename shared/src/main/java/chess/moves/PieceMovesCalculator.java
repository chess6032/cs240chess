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

    protected final ChessBoard board;
    protected final ChessPosition position;
    protected final TeamColor team;

    protected final ChessPiece.PieceType promotionPiece; // only the subclass for the Pawn will use this.

    protected final ArrayList<ChessMove> possibleMoves;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position, TeamColor team) {
        this.board = board;
        this.position = position;
        this.team = team;
        possibleMoves = new ArrayList<ChessMove>();

        promotionPiece = null; // TODO: override this for pawn.
    }

    public ChessPosition calculateRelativePosition(int dRow, int dCol) {
        return new ChessPosition(position.getRow() + dRow, position.getColumn() + dCol);
    }

    public Collection<ChessMove> calculateMoves() {
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

    @Override
    public String toString() {
        return "PieceMovesCalculator{" +
                "board=" + board +
                ", position=" + position +
                ", team=" + team +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PieceMovesCalculator that = (PieceMovesCalculator) o;
        return Objects.equals(board, that.board) && Objects.equals(position, that.position) && team == that.team;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, position, team);
    }
}
