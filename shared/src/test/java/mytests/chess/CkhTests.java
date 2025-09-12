package mytests.chess;

public class CkhTests {

    public static void main(String[] args) {
        CkhChessPositionTests.checkRowDomain();
        System.out.println();
        CkhChessPositionTests.checkColumnDomain();
        System.out.println();

//        CkhChessPositionTests.checkUpperBound();
        CkhChessPositionTests.checkLowerBound();
    }
}
