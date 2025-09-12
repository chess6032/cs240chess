package mytests.chess;

import chess.ChessBoard;

public class CkhChessBoardTests {

    public static void testChessBoardConstructor() {
        ChessBoard board = new ChessBoard();
        System.out.println(board.toString());
    }

    public static void testResetBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.println(board.toString());
    }
}
