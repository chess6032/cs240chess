package mytests.chess;

import chess.ChessPiece;
import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

public class CkhChessPieceTests {

    private static final PieceType[] pieceTypes = {
            PieceType.PAWN, PieceType.ROOK, PieceType.KNIGHT,
            PieceType.BISHOP, PieceType.QUEEN, PieceType.KING,
    };

    public static void checkChessPrint() {
        for (int i = 0; i < pieceTypes.length; ++i) {
            ChessPiece white = new ChessPiece(TeamColor.WHITE, pieceTypes[i]);
            ChessPiece black = new ChessPiece(TeamColor.BLACK, pieceTypes[i]);

            System.out.println("WHITE " + pieceTypes[i] + ": " + white.toString());
            System.out.println("BLACK " + pieceTypes[i] + ": " + black.toString());
            System.out.println();
        }
    }
}
