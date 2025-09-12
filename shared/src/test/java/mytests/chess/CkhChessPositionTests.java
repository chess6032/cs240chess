package mytests.chess;

import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

public class CkhChessPositionTests {
    public static void checkRowDomain() {
        System.out.println("Checking domain of ROW for ChessPosition class...");

        for (int i = -2; i < 13; ++i) {
            System.out.printf("%02d: ", i);
            try {
                new ChessPosition(i, ChessPosition.MIN_POS);
            } catch (IndexOutOfBoundsException err) {
                System.out.print("OUT OF BOUNDS.\n");
                continue;
            }
            System.out.print("in bounds.\n");
        }
    }

    public static void checkColumnDomain() {
        System.out.println("Checking domain of COLUMN for ChessPosition class...");

        for (int i = -2; i < 13; ++i) {
            System.out.printf("%02d: ", i);
            try {
                new ChessPosition(ChessPosition.MIN_POS, i);
            } catch (IndexOutOfBoundsException err) {
                System.out.print("OUT OF BOUNDS.\n");
                continue;
            }
            System.out.print("in bounds.\n");
        }
    }

    public static void checkUpperBound() {
        System.out.println("Checking upper bound (" + ChessPosition.MAX_POS + ")...");
        new ChessPosition(ChessPosition.MAX_POS + 1, ChessPosition.MAX_POS + 1);
    }

    public static void checkLowerBound() {
        System.out.println("Checking lower bound (" + ChessPosition.MIN_POS + ")...");
        new ChessPosition(ChessPosition.MIN_POS - 1, ChessPosition.MIN_POS - 1);
    }
}
