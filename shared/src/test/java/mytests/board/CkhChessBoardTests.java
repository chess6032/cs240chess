package mytests.board;

import chess.*;

import java.util.Iterator;

public class CkhChessBoardTests {
    private static void testChessBoardIterator() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.println(board);
        System.out.println();
//        int i = 0;
//        for (var pair : board) {
//            ++i;
//            System.out.println(i + ": " + pair);
//        }

        ChessBoard copy = new ChessBoard();
        for (var pair : board) {
            copy.addPiece(pair.position(), pair.piece());
        }
        System.out.println(board.equals(copy));
        System.out.println(copy);
    }

    private static void testChessBoardIteratorHasNext() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        var itr = board.iterator();
        for (int i = 0; i < 5; ++i) {
            itr.hasNext();
        }
        System.out.println(itr.next());
    }

    public static void main(String args[]) {
        testChessBoardIterator();
    }
}
