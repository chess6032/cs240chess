package mytests.board;

import chess.*;

public class CkhChessBoardTests {
    private static void testChessBoardIterator() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        for (var piece : board) {
            System.out.println(piece);
        }
    }

    public static void main(String args[]) {
        testChessBoardIterator();
    }
}
