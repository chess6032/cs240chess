package chess.moves.concrete;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.moves.WalkerMovesCalculator;

public class RookMovesCalculator extends WalkerMovesCalculator {

    public RookMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.ROOK;
    }


    @Override
    public void calculateMoves() {
        walkAndAddMoves(1, 0); // up
        walkAndAddMoves(-1, 0); // down
        walkAndAddMoves(0, -1); // left
        walkAndAddMoves(0, 1); // right

        // NOTE: For the down & left loops, where you're DECREMENTING i, you HAVE to start i at 0.
        //       When I started i at 1, the loop automatically stopped when i hit 0. Kinda dumb ngl.
    }
}
