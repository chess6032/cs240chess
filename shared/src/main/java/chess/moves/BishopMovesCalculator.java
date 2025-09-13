package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;

public class BishopMovesCalculator extends PieceMovesCalculator {
    public BishopMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
    }

    @Override
    public void calculateMoves() {
        walkAndAddMoves(1, 1); // up-right
        walkAndAddMoves(-1, 1); // down-right
        walkAndAddMoves(1, -1); // up-left
        walkAndAddMoves(-1, -1); // down-left
    }
}
