package chess;

import java.util.Collection;
import java.util.Objects;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
//        if (!validMoves(move.getStartPosition()).contains(move)) {
//            throw new InvalidMoveException();
//        }

        doMove(move);
    }

    private void doMove(ChessMove move) throws NullPointerException {
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new NullPointerException();
        }
        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        board.removePiece(move.getStartPosition());
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for (var pair : board) {
            if (pair.isNull() || pair.piece().getTeamColor() == teamColor) {
                continue;
            }
            if (pair.piece().pieceMovesEndPositions(board, pair.position()).contains(board.getKingPosition(teamColor))) {
                return true;
            }
        }
        return false;
    }

    private boolean moveEscapesCheck(TeamColor teamColor, ChessMove move) {
        System.out.println("-----------------------");
        System.out.println("Current board:\n" + board);
        ChessBoard savedBoard = board.clone();
        System.out.println("Testing move: " + move);

        doMove(move);

        boolean ret = !isInCheck(teamColor);
        System.out.println("This move " + (ret ? "does" : "does NOT") + " escape check: " + move + "\n" + board);
        assert !board.equals(savedBoard);
        board = savedBoard; // TODO: ??
        assert board.equals(savedBoard);
        System.out.println("- - - - - - - - - - - - -");
        return ret;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        int i = 0;
        System.out.println(board.getPiece(new ChessPosition(4, 6)));
        for (var pair : board) {
            System.out.println(i++ + ": " + pair);
            if (pair.isNull() || pair.piece().getTeamColor() != teamColor) {
                continue;
            }
            for (ChessMove move : pair.piece().pieceMoves(board, pair.position())) {
                try {
                    if (moveEscapesCheck(teamColor, move)) {
                        return false;
                    }
                } catch (NullPointerException e) {
                    System.out.println("***********");
                    System.out.println(board);
                    System.out.println("This move inputted null into ChessBoard.addPiece: " +
                            pair.piece() + " at " + pair.position() + ": " + move);
                    throw e;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        // TODO: make it a copy????
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "ChessGame{ " + teamTurn
                + '\n'
                + board
                + "\n}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame that = (ChessGame) o;
        if (board == null && that.board != null) {
            return false;
        }
        return teamTurn == that.teamTurn && (board == null || board.equals(that.board));
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
