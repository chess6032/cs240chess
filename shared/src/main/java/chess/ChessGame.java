package chess;

import java.util.Collection;



/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;
    private ChessBoard savedBoard;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();

        teamTurn = TeamColor.WHITE;

        savedBoard = null;
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

    private void saveBoard() {
        savedBoard = board.clone();
    }

    private void revertBoard() {
        if (savedBoard == null) {
            throw new RuntimeException("ChessGame.revertBoard called but ChessGame.savedBoard is null");
        }
        board = savedBoard.clone();
        savedBoard = null;
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
        if (!validMoves(move.getStartPosition()).contains(move)) {
            return;
        }
        throw new RuntimeException("Not implemented");
    }


    /**
     * Makes a move in a chess game WITHOUT checking if it's valid
     *
     * @param move chess move to perform
     */
    private void doMove(ChessMove move) {
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), movingPiece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.getKingPosition(teamColor);
        for (var pair : board) {
            if (pair.isNull() || pair.piece().getTeamColor() == teamColor) {
                // skip if space is empty or occupied by peace on king's team
                continue;
            }
            // see if any of piece's moves end on the king's space
            var opposingTeamEndPositions = pair.piece().pieceMovesEndPositions(board, pair.position());
            if (opposingTeamEndPositions.contains(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sees if a move would put king out of checkmate. Does not change board.
     *
     * @param teamColor the team whose king is in check
     * @param move the chess move being tested
     * @return true if performing move does get king out of check, false otherwise
     */
    private boolean moveEscapesCheck(TeamColor teamColor, ChessMove move) {
        boolean doesEscape = false;
        saveBoard();

        doMove(move);
        if (!isInCheck(teamColor)) {
            doesEscape = true;
        }

        revertBoard();
        return doesEscape;
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
        // for all pieces on my team...
        for (var pair : board) {
            if (pair.isNull() || pair.piece().getTeamColor() != teamColor) {
                continue;
            }
            // ...look at all their moves...
            for (var move : pair.piece().pieceMoves(board, pair.position())) {
                // ...and see if one of them gets me out of checkmate.
                if (moveEscapesCheck(teamColor, move)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board; // TODO: set to clone????
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board; // TODO: return copy????
    }
}
