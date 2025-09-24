package mytests.board;

import chess.*;

public class CkhChessBoardTests {
    private static void testChessBoardIterator() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        for (var pair : board) {
            System.out.println(pair);
        }
    }

    public static void main(String args[]) {
        testChessBoardIterator();
    }
}
