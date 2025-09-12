package mytests.chess;

public class CkhTests {

    private static void chessPositionTests() {
        CkhChessPositionTests.checkRowDomain();
        System.out.println();
        CkhChessPositionTests.checkColumnDomain();
        System.out.println();

//        CkhChessPositionTests.checkUpperBound();
        CkhChessPositionTests.checkLowerBound();
    }

    private static void chessBoardTests() {
        System.out.println("Testing constructor...");
        CkhChessBoardTests.testChessBoardConstructor();
        System.out.println("Testing resetBoard...");
        CkhChessBoardTests.testResetBoard();
    }

    public static void main(String[] args) {
        chessBoardTests();
    }
}
