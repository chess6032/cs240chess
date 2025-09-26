package chess.moves.concrete;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;
import chess.moves.AbstractMovesCalculator;

public class PawnMovesCalculator extends AbstractMovesCalculator {

    private final ChessPosition myPosition;
    private final TeamColor myTeam;

    public PawnMovesCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor team) {
        super(board, position, team);
        myTeam = team;
        myPosition = position;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.PAWN;
    }

    private void addMoveIfDiagonalCapture(int dRow, int dCol) {
        ChessPosition position = calculateRelativePosition(dRow, dCol);
        if (ChessBoard.isPositionOutOfBounds(position))
            return;

        ChessPiece pieceAtPos = board.getPiece(position);
        System.out.println("PawnMovesCalculator.addMoveIfDiagonalCapture: " + pieceAtPos + " at " + position + ". FKM: " + board.getFauxKingMove());
        if (pieceAtPos != null && pieceAtPos.getTeamColor() != myTeam) {
            addMove(position);
        }
    }

    @Override
    protected boolean addMove(ChessPosition newPosition) {
        if (newPosition.getRow() == 1 || newPosition.getRow() == ChessBoard.getBoardWidth()) { // can promote?
            // add all possible promotions
            for (PieceType type : PieceType.values()) {
                if (type != PieceType.KING && type != PieceType.PAWN)
                    super.addMove(newPosition, type);
            }
            return false;
        }

        super.addMove(newPosition, null);
        return true;
    }

    @Override
    public void calculateMoves() {
        int forward = myTeam == TeamColor.WHITE ? 1 : -1; // forward = up if white, down if black.

        if (addMoveIfRelativeSpaceEmpty(forward, 0)) { // forward one space.
            if (myPosition.getRow() == 2 || myPosition.getRow() == ChessBoard.getBoardWidth() - 1) {
                addMoveIfRelativeSpaceEmpty(forward * 2, 0); // forward two spaces.
            }
        }

        addMoveIfDiagonalCapture(forward, -1); // capture forward-left.
        addMoveIfDiagonalCapture(forward, 1); // capture forward-right.

        System.out.println(this);
    }
}
