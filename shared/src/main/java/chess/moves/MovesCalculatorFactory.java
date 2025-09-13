package chess.moves;

// decides what move class to calculate moves from.

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;

public class MovesCalculatorFactory {

    // return instance of appropriate piece moves calculator class.
    public static PieceMovesCalculator returnPieceMovesCalculator(PieceType type, ChessBoard board, ChessPosition position, TeamColor team) {
        switch (type) {
            case PieceType.KING:
                return new KingMovesCalculator(board, position, team);
            default:
                return new PieceMovesCalculator(board, position, team); // should never happen.
        }

    }
}
