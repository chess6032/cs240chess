package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;

import java.util.Collection;
import java.util.HashSet;

public class PieceMovesCalculator {

    private final ChessBoard board;
    private final ChessPosition position;
    private final TeamColor team;

    private final ChessPiece.PieceType promotionPiece; // only the subclass for the Pawn will use this.

    private final HashSet<ChessMove> possibleMoves;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position, TeamColor team) {
        this.board = board;
        this.position = position;
        this.team = team;
        possibleMoves = new HashSet<ChessMove>();

        promotionPiece = null; // TODO: override this for pawn.
    }

    public Collection<ChessMove> getPossibleMoves() {
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

    private void addMove(ChessPosition newPosition) {
            possibleMoves.add(new ChessMove(position, newPosition, promotionPiece));
    }

    protected boolean addMoveIfSpaceEmpty(ChessPosition newPosition) {
        if (board.isPositionOutOfBounds(newPosition))
            return false;

        if (board.getPiece(newPosition) == null){
            addMove(newPosition);
            return true;
        }

        return false;
    }

    protected boolean addMoveIfRelativeSpaceEmpty(int dRow, int dCol) {
        return addMoveIfSpaceEmpty(calculateRelativePosition(dRow, dCol));
    }

    // "space available" = space is empty OR occupied by OPPONENT team's piece.
    protected boolean addMoveIfSpaceAvailable(ChessPosition newPosition) {

        if (board.isPositionOutOfBounds(newPosition))
            return false;

        if (addMoveIfSpaceEmpty(newPosition))
            return true;

        ChessPiece that = board.getPiece(newPosition);
        if (that.getTeamColor() != team && that.getPieceType() != PieceType.KING) {
            // TODO: do I even need to check if opposing piece is King? (is that handled by ChessGame??)
            addMove(newPosition);
            return true;
        }

        return false;
    }

    protected boolean addMoveIfRelativeSpaceAvailable(int dRow, int dCol) {
        return addMoveIfSpaceAvailable(calculateRelativePosition(dRow, dCol));
    }
}
