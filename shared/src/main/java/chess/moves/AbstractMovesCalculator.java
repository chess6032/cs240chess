package chess.moves;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public abstract class AbstractMovesCalculator implements PieceMovesCalculator {

    protected final ChessBoard board;
    protected final ChessPosition position;
    private final TeamColor team;

    protected final HashSet<ChessMove> possibleMoves;

    protected AbstractMovesCalculator(ChessBoard board, ChessPosition position, TeamColor team) {
        this.board = board;
        System.out.println("AbstractMovesCalculator position: " + position);
        this.position = new ChessPosition(position.getRow(), position.getColumn());
        this.team = team;

        possibleMoves = new HashSet<>();
    }

    public final Collection<ChessMove> getPossibleMoves() {
        calculateMoves();
        return possibleMoves;
    }

    protected abstract void calculateMoves();

    protected ChessPosition calculateRelativePosition(int dRow, int dCol) {
        return new ChessPosition(position.getRow() + dRow, position.getColumn() + dCol);
    }

    protected boolean addMove(ChessPosition newPosition) {
        return addMove(newPosition, null);
    }

    protected boolean addMove(ChessPosition newPosition, PieceType promotion) {
        if (ChessBoard.isPositionOutOfBounds(newPosition)) {
            return false;
        }

        possibleMoves.add(new ChessMove(position, newPosition, promotion));
        return true;
    }

    protected boolean addMoveIfRelativeSpaceEmpty(int dRow, int dCol) {
        ChessPosition position = calculateRelativePosition(dRow, dCol);
        if (ChessBoard.isPositionOutOfBounds(position)) {
            return false;
        }

        if (board.getPiece(position) == null) {
            addMove(position);
            return true;
        }

        return false; // space is filled.
    }

    protected boolean addMoveIfRelativeSpaceAvailable(int dRow, int dCol) {
        if (addMoveIfRelativeSpaceEmpty(dRow, dCol)) {
            return true; // space is empty
        }

        ChessPosition position = calculateRelativePosition(dRow, dCol);
        if (ChessBoard.isPositionOutOfBounds(position)) {
            return false;
        }

        if (board.getPiece(position).getTeamColor() != team) {
            addMove(position);
            return true; // space is capturable.
        }

        return false; // space is occupied by team piece.
    }
}
