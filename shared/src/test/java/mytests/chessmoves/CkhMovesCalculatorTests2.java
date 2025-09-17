package mytests.chessmoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;
import chess.moves.PieceMovesCalculator;

public class CkhMovesCalculatorTests2 {

    private static void testPieceMovesCalcGetType() {
        for (PieceType type : PieceType.values()) {
            PieceMovesCalculator calc = PieceMovesCalculator.movesCalculatorFactory(type, new ChessBoard(),
                    new ChessPosition(1, 1), ChessGame.TeamColor.WHITE);
            assert type == calc.getPieceType();
        }
    }

    public static void main(String[] args) {
        testPieceMovesCalcGetType();
    }
}
