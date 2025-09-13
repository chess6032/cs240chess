package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class QueenMovesCalculator extends PieceMovesCalculator {

    private final BishopMovesCalculator bishop;
    private final RookMovesCalculator rook;

    public QueenMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
        bishop = new BishopMovesCalculator(board, position, team);
        rook = new RookMovesCalculator(board, position, team);
    }

    @Override
    public void calculateMoves() {
        bishop.calculateMoves();
        rook.calculateMoves();
        for (ChessMove move : bishop.getPossibleMoves()) {
            addMoveIfSpaceAvailable(move.getEndPosition());
        }
        for (ChessMove move : rook.getPossibleMoves()) {
            addMoveIfSpaceAvailable(move.getEndPosition());
        }
    }
}
