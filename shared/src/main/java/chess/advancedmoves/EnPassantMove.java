package chess.advancedmoves;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Objects;

public class EnPassantMove extends ChessMove{

    private final ChessPosition victimPawnPosition;

    public EnPassantMove(ChessPosition startPosition, ChessPosition endPosition, ChessPosition victimPawnPosition) {
        super(startPosition, endPosition, null);
        this.victimPawnPosition = victimPawnPosition;
    }

    public ChessPosition getVictimPawnPosition() {
        return victimPawnPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            if (o instanceof ChessMove) {
                return super.equals(o);
            }
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        EnPassantMove that = (EnPassantMove) o;
        return victimPawnPosition.equals(that.victimPawnPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), victimPawnPosition);
    }

    @Override
    public String toString() {
        return "EnPassantMove{" + super.toString() +
                "victimPawnPosition=" + victimPawnPosition +
                '}';
    }
}
