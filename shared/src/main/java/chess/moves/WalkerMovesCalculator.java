package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;

public abstract class WalkerMovesCalculator extends AbstractMovesCalculator {

    public WalkerMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
    }

    protected final void walkAndAddMoves(int dRow, int dCol) {
        int i = 0;
        int j = 0;
        while (addMoveIfRelativeSpaceEmpty(i += dRow, j += dCol)) {}; // walk until space occupied.
        addMoveIfRelativeSpaceAvailable(i, j); // see if occupied space is capturable.
    }
}
