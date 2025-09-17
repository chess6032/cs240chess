package chess.moves.concrete;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
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
    public ChessPiece.PieceType getPieceType() {
        return ChessPiece.PieceType.QUEEN;
    }

    private void addMovesToMine(PieceMovesCalculator piece) {
        for (ChessPosition position : PieceMovesCalculator.getFinalPositions(piece)) {
            addMove(position);
        }
    }

    @Override
    public void calculateMoves() {
        addMovesToMine(bishop);
        addMovesToMine(rook);
    }
}
