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

    protected ChessPiece.PieceType promotionPiece; // only the subclass for the Pawn will use this.

    private final HashSet<ChessMove> possibleMoves;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position, TeamColor team) {
        this.board = board;
        this.position = position;
        this.team = team;
        possibleMoves = new HashSet<ChessMove>();

        promotionPiece = null; // TODO: override this for pawn.
    }

    protected boolean amIWhite() {
        // for pawn to tell which way is forward.
        return team == TeamColor.WHITE;
    }

    protected boolean canIBounce() {
        // for pawns to tell if they can move forward 2 spaces.
        return position.getRow() == 2 || position.getRow() == ChessBoard.getBoardWidth() - 2;
        // NOTE: I'm just checking if pawn is on 2nd row or 2nd-to-last row,
        //       instead of checking if the pawn has moved.
        //       This should work fine because if a pawn is on the 2nd-to-last row,
        //       when it calculates its moves, the forward-2-spaces moves
        //       will be out of bounds.
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

    public ChessPosition calculateRelativePosition(int dRow, int dCol) {
        ChessPosition relative = new ChessPosition(position.getRow() + dRow, position.getColumn() + dCol);
//        System.out.println(position + " + (" + dRow + ", " + dCol + ") = " + relative);
        return relative;
    }

    public void calculateMoves() {
        // OVERRIDE THIS IN SUBCLASSES
        throw new RuntimeException("calculateMoves invoked on PieceMovesCalculator (the move calculator superclass). " +
                "Did you use MoveCalculatorFactory to get your PieceMovesCalculator?" +
                "(If so, check that MoveCalculatorFactory is working properly. " +
                "It should return a SUBCLASS of PieceMovesCalculator.)");
    }

    private void addMove(ChessPosition newPosition) {
        possibleMoves.add(new ChessMove(position, newPosition, promotionPiece));
    }

    private boolean addMoveIfSpaceEmpty(ChessPosition newPosition) {

        if (this instanceof PawnMovesCalculator && board.isPositionOutOfBounds(newPosition)) {
            System.out.println("ooooh how exciting! I might get to promote! " + position.toString());
            if (newPosition.getRow() == 1 || newPosition.getRow() == ChessBoard.getBoardWidth()) {
                promotionPiece = PieceType.QUEEN; // TODO: do I need to implement promoting to rook as well?
            }
        }

        if (board.isPositionOutOfBounds(newPosition))
            return false;

        if (board.getPiece(newPosition) == null){
            addMove(newPosition);
            return true;
        }

        return false;
    }

    protected boolean addMoveIfRelativeSpaceEmpty(int dRow, int dCol) {
        return addMoveIfSpaceEmpty(calculateRelativePosition(dRow, dCol));
    }

    // returns true ONLY IF SPACE WAS EMPTY
    // yes that's kind of sus, but it makes things really easy for rook, bishop, & queen.
    protected boolean addMoveIfSpaceAvailable(ChessPosition newPosition) {

        if (board.isPositionOutOfBounds(newPosition))
            return false;

        if (addMoveIfSpaceEmpty(newPosition))
            return true;

        ChessPiece that = board.getPiece(newPosition);
        if (that.getTeamColor() != team && that.getPieceType() != PieceType.KING) {
            // TODO: do I even need to check if opposing piece is King? (is that handled by ChessGame??)
            addMove(newPosition);
            return false; // (sus)
        }

//        System.out.println("Move not added: " + newPosition);

        return false;
    }

    protected boolean addMoveIfRelativeSpaceAvailable(int dRow, int dCol) {
        return addMoveIfSpaceAvailable(calculateRelativePosition(dRow, dCol));
    }

    protected void walkAndAddMoves(int dRow, int dCol) {
        int i = 0;
        int j = 0;
        while (addMoveIfRelativeSpaceAvailable(i += dRow, j += dCol));
    }

    protected boolean addMoveIfRelativeSpaceCapturable(int dRow, int dCol) {
        ChessPosition position = calculateRelativePosition(dRow, dCol);
        if (board.isPositionOutOfBounds(position))
            return false;

        ChessPiece pieceAtPos = board.getPiece(position);
        if (pieceAtPos != null && pieceAtPos.getTeamColor() != team) {
            addMove(position);
            return true;
        }
        return false;
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
