package chess.moves;

// decides what move class to calculate moves from.

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;

public class MovesCalculatorFactory {

    // return instance of appropriate piece moves calculator class.
    public static PieceMovesCalculator returnPieceMovesCalculator(PieceType type, ChessBoard board, ChessPosition position, TeamColor team) {
        return switch (type) {
            case PieceType.KING -> new KingMovesCalculator(board, position, team);
            case PieceType.ROOK -> new RookMovesCalculator(board, position, team);
            case PieceType.BISHOP -> new BishopMovesCalculator(board, position, team);
            case PieceType.QUEEN -> new QueenMovesCalculator(board, position, team);
            default ->
                    throw new RuntimeException("(MovesCalculatorFactory) No MovesCalculator class matched inputted ChessPiece.PieceType");
        };

    }
}
