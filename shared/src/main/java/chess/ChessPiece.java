package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import chess.ChessGame.TeamColor;
import chess.moves.PieceMovesCalculator;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final TeamColor team;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        team = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return team;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return PieceMovesCalculator.pieceMoves(type, board, myPosition, team);
    }

    public Collection<ChessPosition> pieceMoveEndPositions(ChessBoard board, ChessPosition myPosition) {
        return PieceMovesCalculator.getFinalPositions(pieceMoves(board, myPosition));
    }

    private static final Map<PieceType, Character> pieceTranslation = new HashMap<>();
    static {
        // TODO: is there a better way to do this?
        pieceTranslation.put(PieceType.KING, 'k');
        pieceTranslation.put(PieceType.QUEEN, 'q');
        pieceTranslation.put(PieceType.BISHOP, 'b');
        pieceTranslation.put(PieceType.KNIGHT, 'n');
        pieceTranslation.put(PieceType.ROOK, 'r');
        pieceTranslation.put(PieceType.PAWN, 'p');
    }

    @Override
    public String toString() {
        char pieceChar = pieceTranslation.get(type); // get char corresponding to piece type.
        if (team == TeamColor.WHITE) // capitalize character if piece is black.
            pieceChar = Character.toUpperCase(pieceChar);
        return String.valueOf(pieceChar); // convert char to string and return it.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        ChessPiece that = (ChessPiece) o;
        return team == that.team
            && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, type);
    }

}
