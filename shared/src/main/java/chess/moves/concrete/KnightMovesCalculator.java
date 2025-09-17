package chess.moves.concrete;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.moves.AbstractMovesCalculator;

public class KnightMovesCalculator extends AbstractMovesCalculator {
    public KnightMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.KNIGHT;
    }

    @Override
    public void calculateMoves() {
        addMoveIfRelativeSpaceAvailable(2, -1); // up 2, left 1
        addMoveIfRelativeSpaceAvailable(2, 1); // up 2, right 1

        addMoveIfRelativeSpaceAvailable(-2, -1); // down 2, left 1
        addMoveIfRelativeSpaceAvailable(-2, 1); // down 2, right 1

        addMoveIfRelativeSpaceAvailable(1, -2); // up 1, left 2
        addMoveIfRelativeSpaceAvailable(1, 2); // up 1, right 2

        addMoveIfRelativeSpaceAvailable(-1, -2); // down 1, left 2
        addMoveIfRelativeSpaceAvailable(-1, 2); // down 1, right 2
    }
}
