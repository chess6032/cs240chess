package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;

public class RookMovesCalculator extends PieceMovesCalculator{

    public RookMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
//        System.out.println(board.toString());
    }

    @Override
    public void calculateMoves() {
        // up
        System.out.println("up");
        int i = 0;
        while (addMoveIfRelativeSpaceAvailable(++i, 0));

        // down
        System.out.println("down");
        i = 0;
        while (addMoveIfRelativeSpaceAvailable(--i, 0));

        System.out.println("left");
        // left
        i = 0;
        while (addMoveIfRelativeSpaceAvailable(0, --i));

        // right
        System.out.println("right");
        i = 0;
        while (addMoveIfRelativeSpaceAvailable(0, ++i));

        System.out.println(this);

        // NOTE: For the down & left loops, where you're DECREMENTING i, you HAVE to start i at 0.
        //       When I started i at 1, the loop automatically stopped when i hit 0. Kinda dumb ngl.
    }
}
