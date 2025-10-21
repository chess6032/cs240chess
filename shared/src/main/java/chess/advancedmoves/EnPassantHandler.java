package chess.advancedmoves;

import chess.*;

import java.util.Collection;

import static chess.ChessPiece.PieceType.PAWN;

public class EnPassantHandler {

    /**
     * (Assumes piece at attackingPawnPosition is a pawn)
     *
     * @param board chess board
     * @param lastMove last move made on board
     * @param attackingPawnPosition (Assumes pawn is at attacking pawn position)
     * @return en passant move if en passant doable, or null if not
     */
    public static EnPassantMove calculateEnPassantMove(ChessBoard board, ChessMove lastMove, ChessPosition attackingPawnPosition) {
        if (lastMove == null) {
//            System.out.println("calculateEnPassantMove: lastMove is null");
            return null;
        }

        ChessPosition victimPawnPosition = lastMove.getEndPosition();
        if (attackingPawnPosition.getRow() != victimPawnPosition.getRow()) {
//            System.out.println("calculateEnPassantMove: attacker and victim not on same row");
            return null;
        }
        if (attackingPawnPosition.getColumn() - 1 != victimPawnPosition.getColumn()
                && attackingPawnPosition.getColumn() + 1 != victimPawnPosition.getColumn()) {
//            System.out.println("calculateEnPassantMove: victim is not to left or right of attacker");
            return null;
        }

        ChessPiece victimPawn = board.getPiece(victimPawnPosition);
        ChessPiece attackingPawn = board.getPiece(attackingPawnPosition);

        if (victimPawn.getPieceType() != PAWN) {
            return null;
        }

        if (victimPawn.getTeamColor() == attackingPawn.getTeamColor()) {
//            System.out.println("calculateEnPassantMove: victim is not right piece");
            return null;
        }

        int forward = attackingPawn.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        return new EnPassantMove(attackingPawnPosition, new ChessPosition(
                victimPawnPosition.getRow()+forward, victimPawnPosition.getColumn()), victimPawnPosition);
    }
}
