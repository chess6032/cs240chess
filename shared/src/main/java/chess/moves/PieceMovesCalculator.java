package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;

import java.util.Collection;
import java.util.HashSet;

public class PieceMovesCalculator {

    private final ChessBoard board;
    private final ChessPosition position;
    private final TeamColor team;

    private final HashSet<ChessMove> possibleMoves;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position, TeamColor team) {
        this.board = board;
        this.position = position;
        this.team = team;
        possibleMoves = new HashSet<ChessMove>();
    }

    protected ChessBoard getMyBoard() {
        return board;
    }

    public Collection<ChessMove> getPossibleMoves() {
        return possibleMoves;
        // TODO: return copy???
    }

    public static Collection<ChessPosition> getFinalPositions(Collection<ChessMove> moves) {
        HashSet<ChessPosition> positions = new HashSet<>();
        for (ChessMove move : moves) {
            positions.add(move.getEndPosition());
        }
        return (Collection<ChessPosition>) positions;
    }

    protected ChessPosition calculateRelativePosition(int dRow, int dCol) {
//        System.out.println(position + " + (" + dRow + ", " + dCol + ") = " + relative);
        return new ChessPosition(position.getRow() + dRow, position.getColumn() + dCol);
    }

    public void calculateMoves() {
        // OVERRIDE THIS IN SUBCLASSES
        throw new RuntimeException("calculateMoves not overridden, " +
                "or calculateMoves invoked on PieceMovesCalculator superclass.");
    }

    protected void addMove(ChessPosition newPosition, PieceType promotion) {
        possibleMoves.add(new ChessMove(position, newPosition, promotion));
    }

    protected void addMove(ChessPosition newPosition) {
        addMove(newPosition, null);
    }

    protected boolean addMoveIfRelativeSpaceEmpty(int dRow, int dCol) {
        ChessPosition position = calculateRelativePosition(dRow, dCol);
        if (ChessBoard.isPositionOutOfBounds(position))
            return false;

        if (board.getPiece(position) == null){
            addMove(position);
            return true;
        }

        return false;
    }

    protected boolean addMoveIfRelativeSpaceAvailable(int dRow, int dCol) {
        ChessPosition position = calculateRelativePosition(dRow, dCol);
        if (ChessBoard.isPositionOutOfBounds(position))
            return false;

        ChessPiece pieceAtPos = board.getPiece(position);
        if (pieceAtPos == null || pieceAtPos.getTeamColor() != team) {
            addMove(position);
            return true;
        }

        return false;
    }

    protected void walkAndAddMoves(int dRow, int dCol) {
        int i = 0;
        int j = 0;
        while (addMoveIfRelativeSpaceEmpty(i += dRow, j += dCol)); // walk until space occupied.
        addMoveIfRelativeSpaceAvailable(i, j); // see if space is capturable.
    }



    @Override
    public String toString() {
        return displayOnBoard() + "\n" + displayMovesAsList();
    }

    public String displayOnBoard() {
        return board.toString(PieceMovesCalculator.getFinalPositions(possibleMoves));
    }

    public String displayMovesAsList() {
        StringBuilder sb = new StringBuilder(" ");
        for (ChessMove move : possibleMoves) {
            sb.append(move.getEndPosition());
            sb.append("\n");
        }
        return sb.toString();
    }
}
