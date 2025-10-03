package mytests.chessmoves;


import chess.*;
import chess.ChessPiece.PieceType;
import chess.moves.PieceMovesCalculator;
import chess.advancedmoves.EnPassantMove;

public class CkhMovesCalculatorTests2 {

    private static void testPieceMovesCalcGetType() {
        for (PieceType type : PieceType.values()) {
            PieceMovesCalculator calc = PieceMovesCalculator.movesCalculatorFactory(type, new ChessBoard(),
                    new ChessPosition(1, 1), ChessGame.TeamColor.WHITE);
            assert type == calc.getPieceType();
        }
    }

    private static void testEnPassantEquality() {
        ChessMove a = new ChessMove(new ChessPosition(4, 8), new ChessPosition(3, 7), null);
        EnPassantMove b = new EnPassantMove(new ChessPosition(4, 8), new ChessPosition(3, 7), new ChessPosition(3, 8));
        System.out.println(a + " equals " + b + "? - " + (a.equals(b)));
        System.out.println(b + " equals " + a + "? - " + (b.equals(a)));
    }

    public static void main(String[] args) {
//        testPieceMovesCalcGetType();
        testEnPassantEquality();
    }
}
