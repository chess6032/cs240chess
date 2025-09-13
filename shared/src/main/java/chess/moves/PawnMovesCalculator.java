package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;

public class PawnMovesCalculator extends PieceMovesCalculator{
    public PawnMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
//        promotionPiece = PieceType.QUEEN; // promote to queen by default.
    }

    public void setPromotionPiece(PieceType newType) {
        promotionPiece = newType;
        // I have no idea when the player decides what to promote the pawn to,
        // so for now I'm going to leave it available as a setter method, usable whenever.
    }

    @Override
    public void calculateMoves() {
        int forward = amIWhite() ? 1 : -1;

        addMoveIfRelativeSpaceEmpty(forward, 0); // forward 1
        if (canIBounce())
            addMoveIfRelativeSpaceEmpty(forward * 2, 0); // forward 2

        addMoveIfRelativeSpaceCapturable(forward, -1); // capture forward-left
        addMoveIfRelativeSpaceCapturable(forward, 1); // capture forward-right
    }

}
