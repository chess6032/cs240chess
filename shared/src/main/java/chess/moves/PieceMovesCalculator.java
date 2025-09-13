package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;

import java.util.Collection;
import java.util.Objects;

public class PieceMovesCalculator {

    private final ChessBoard board;
    private final ChessPosition position;
    private final TeamColor team;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position, TeamColor team) {
        this.board = board;
        this.position = position;
        this.team = team;
    }

    public Collection<ChessMove> calculateMoves() {
        // OVERRIDE THIS IN SUBCLASSES
        throw new RuntimeException("calculateMoves invoked on PieceMovesCalculator (the move calculator superclass). " +
                "Did you use MoveCalculatorFactory to get your PieceMovesCalculator?" +
                "(If so, check that MoveCalculatorFactory is working properly. " +
                "It should return a SUBCLASS of PieceMovesCalculator.)");
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
