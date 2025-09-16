package chess.moves.concrete;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import chess.moves.PieceMovesCalculator;
import chess.moves.WalkerMovesCalculator;

public class QueenMovesCalculator extends WalkerMovesCalculator {

    private final BishopMovesCalculator bishop;
    private final RookMovesCalculator rook;

    public QueenMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
        bishop = new BishopMovesCalculator(board, position, team);
        rook = new RookMovesCalculator(board, position, team);
    }

    @Override
    public void calculateMoves() {
        walkAndAddMoves(1, 0); // up
        walkAndAddMoves(-1, 0); // down
        walkAndAddMoves(0, -1); // left
        walkAndAddMoves(0, 1); // right

        walkAndAddMoves(1, 1); // up-right
        walkAndAddMoves(-1, 1); // down-right
        walkAndAddMoves(1, -1); // up-left
        walkAndAddMoves(-1, -1); // down-left
    }
}
