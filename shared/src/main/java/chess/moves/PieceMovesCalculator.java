package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;
import chess.moves.concrete.*;

import java.util.Collection;
import java.util.HashSet;

public interface PieceMovesCalculator {

    static Collection<ChessMove> pieceMoves(PieceType type, ChessBoard board, ChessPosition position, TeamColor team) {
        PieceMovesCalculator calculator = movesCalculatorFactory(type, board, position, team);
        return calculator.getPossibleMoves();
    }

    // return instance of appropriate piece moves calculator class.
    static PieceMovesCalculator movesCalculatorFactory(PieceType type, ChessBoard board, ChessPosition position, TeamColor team) {
        return switch(type) {
            case PieceType.KING -> new KingMovesCalculator(board, position, team);
            case PieceType.ROOK -> new RookMovesCalculator(board, position, team);
            case PieceType.BISHOP -> new BishopMovesCalculator(board, position, team);
            case PieceType.QUEEN -> new QueenMovesCalculator(board, position, team);
            case PieceType.KNIGHT -> new KnightMovesCalculator(board, position, team);
            case PieceType.PAWN -> new PawnMovesCalculator(board, position, team);
        };
    }

    static Collection<ChessPosition> getFinalPositions(Collection<ChessMove> moves) {
        // this is mainly used for testing.
        HashSet<ChessPosition> positions = new HashSet<>();
        for (ChessMove move : moves) {
            positions.add(move.getEndPosition());
        }
        return (Collection<ChessPosition>) positions;
    }

    Collection<ChessMove> getPossibleMoves();

    PieceType getPieceType();
}
