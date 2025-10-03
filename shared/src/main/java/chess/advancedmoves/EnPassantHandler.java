package chess.advancedmoves;

import chess.*;

import java.util.Collection;

import static chess.ChessPiece.PieceType.PAWN;

public class EnPassantHandler {

    private final ChessBoard board;
    private ChessMove lastMove;

    public EnPassantHandler(ChessBoard board) {
        this.board = board;
    }

    public void setLastMove(ChessMove move) {
        lastMove = move;
    }

    private boolean lastMoveWasPawn() {
        if (lastMove == null) {
            return false;
        }
//        System.out.println(board);
        System.out.println("Last move: " + lastMove);
        return board.getPiece(lastMove.getEndPosition()).getPieceType() == PAWN;
    }

    private int forward(ChessPosition pawnPosition) {
        ChessGame.TeamColor team = board.getPiece(pawnPosition).getTeamColor();
        return team == ChessGame.TeamColor.WHITE ? 1 : -1;
    }

    public ChessMove enPassantMove(ChessPosition attackingPawnPosition) {
        if (!lastMoveWasPawn()
                || board.getPiece(attackingPawnPosition) == null
                || board.getPiece(attackingPawnPosition).getPieceType() != PAWN) {
            return null;
        }

        ChessPosition victimPawnPosition = lastMove.getEndPosition();
        if (attackingPawnPosition.getRow() != victimPawnPosition.getRow()) {
            return null;
        }

        if (attackingPawnPosition.getColumn() - victimPawnPosition.getColumn() == 1
            || attackingPawnPosition.getColumn() - victimPawnPosition.getColumn() == -1) {
            return new ChessMove(attackingPawnPosition,
                    new ChessPosition(
                        victimPawnPosition.getRow() + forward(attackingPawnPosition),
                        victimPawnPosition.getColumn()),
                    null);
        }

        return null;
    }
}
