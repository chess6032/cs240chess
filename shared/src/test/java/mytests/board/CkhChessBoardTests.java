package mytests.board;

import chess.*;

import java.util.Iterator;

public class CkhChessBoardTests {
    private static void testChessBoardIterator() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.println(board);
        System.out.println();
        int i = 0;
        for (var pair : board) {
            ++i;
            System.out.println(i + ": " + pair);
        }
    }

    private static void testChessBoardIteratorHasNext() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        var itr = board.iterator();
        itr.hasNext();
        itr.hasNext();
        itr.hasNext();
        itr.hasNext();
        itr.hasNext();
        itr.hasNext();
        System.out.println(itr.next());
    }

    public static void main(String args[]) {
        testChessBoardIterator();
    }
}
