package mytests.board;

import chess.*;
import java.util.Random;


import static chess.ChessPiece.PieceType.*;
import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

public class CkhChessBoardTests {

    private static PieceType createRandomPieceType(Random r) {
        int i = r.nextInt(PieceType.values().length);
        return PieceType.values()[i];
    }

    private static TeamColor createRandomTeamColor(Random r) {
        int i = r.nextInt(TeamColor.values().length);
        return TeamColor.values()[i];
    }

    private static ChessPiece createRandomPiece(Random r) {
        return new ChessPiece(createRandomTeamColor(r), createRandomPieceType(r));
    }

    private static ChessBoard createRandomBoard(Random r) {
        var board = new ChessBoard();
        int numOfPieces = r.nextInt(10);
        System.out.println("number of pieces: " + numOfPieces);
        for (int i = 0; i < numOfPieces; ++i) {
            var pos = new ChessPosition(r.nextInt(8)+1, r.nextInt(8)+1);
            while (board.getPiece(pos) != null) {
                pos = new ChessPosition(r.nextInt(8)+1, r.nextInt(8)+1);
            }
            board.addPiece(pos, createRandomPiece(r));
        }
        return board;


    }

    private static void testIteratorOnRandom() {
        var board = createRandomBoard(new Random());
        System.out.println(board);
        System.out.println();

        int i = 0;
        for (var pair : board) {
            System.out.println(++i + ": " + pair);
        }
    }

    private static void testIteratorOnFull() {
        var board = new ChessBoard();
        var r = new Random();
        for (int i = 0; i < ChessBoard.getBoardWidth(); ++i) {
            for (int j = 0; j < ChessBoard.getBoardWidth(); ++j) {
                board.addPiece(new ChessPosition(i+1, j+1), createRandomPiece(r));
            }
        }

        System.out.println(board);
        System.out.println();

        int i = 0;
        for (var pair : board) {
            System.out.println(++i + ": " + pair);
        }
    }

    private static void testIteratorOnEmpty() {
        var board = new ChessBoard();
        System.out.println(board);

        for (var pair : board) {
            System.out.println(pair);
        }
    }

    private static void testIteratorOnReset() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.println(board);
        System.out.println();

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
        testIteratorOnFull();
    }
}
