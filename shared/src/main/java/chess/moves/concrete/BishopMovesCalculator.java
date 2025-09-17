package chess.moves.concrete;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.moves.WalkerMovesCalculator;

public class BishopMovesCalculator extends WalkerMovesCalculator {
    public BishopMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.BISHOP;
    }

    @Override
    public void calculateMoves() {
        walkAndAddMoves(1, 1); // up-right
        walkAndAddMoves(-1, 1); // down-right
        walkAndAddMoves(1, -1); // up-left
        walkAndAddMoves(-1, -1); // down-left
    }
}
