package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends PieceMovesCalculator {
    public KingMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
    }

    @Override
    public Collection<ChessMove> calculateMoves() {
        Collection<ChessMove> possibleMoves = new ArrayList<ChessMove>();

        for (int i = -1; i <= 1; ++i) {
            // check for empty spaces on row above and below King:
            addMoveIfRelativeSpaceEmpty(1, i);
            addMoveIfRelativeSpaceEmpty(-1, i);
        }
        // check for empty spaces to the left & right of King:
        addMoveIfRelativeSpaceEmpty(0, 1);
        addMoveIfRelativeSpaceEmpty(0, -1);

        return possibleMoves;
    }
}
