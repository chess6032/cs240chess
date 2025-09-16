package chess.moves.concrete;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import chess.moves.AbstractMovesCalculator;
import chess.moves.PieceMovesCalculator;

public class KingMovesCalculator extends AbstractMovesCalculator {
    public KingMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
    }

    @Override
    public void calculateMoves() {
        for (int i = -1; i <= 1; ++i) {
            // check for empty spaces on row above and below King:
            addMoveIfRelativeSpaceAvailable(1, i);
            addMoveIfRelativeSpaceAvailable(-1, i);
        }
        // check for empty spaces to the left & right of King:
        addMoveIfRelativeSpaceAvailable(0, 1);
        addMoveIfRelativeSpaceAvailable(0, -1);
    }
}
