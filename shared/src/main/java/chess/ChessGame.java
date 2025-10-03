package chess;

import chess.advancedmoves.EnPassantHandler;

import java.util.Collection;

import java.util.Collections;
import java.util.Objects;
import java.util.Stack;

import chess.ChessPiece.PieceType;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;
    private final Stack<ChessBoard> savedBoards;
    private ChessMove lastMove;

//    private final EnPassantHandler enPassHandler;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();

//        enPassHandler = new EnPassantHandler(board);

        teamTurn = TeamColor.WHITE;

        savedBoards = new Stack<>();
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

    private void swapTeam() {
        teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Push a copy of board to savedBoards
     */
    private void saveBoard() {
        savedBoards.add(board.clone());
    }

    /**
     * Get a copy of the board while also saving it.
     *
     * @return last ChessBoard in savedBoards (i.e. board you just saved))
     */
    private ChessBoard saveBoardAndGetCopy() {
        saveBoard();
        return savedBoards.getLast();
    }

    /**
     * Revert board to most recently saved ChessBoard
     */
    private void revertBoard() {
        if (savedBoards.empty()) {
            throw new RuntimeException("ChessGame.revertBoard called but ChessGame.savedBoards is empty");
        }
        board = savedBoards.peek();
        savedBoards.pop();
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            throw new RuntimeException("erm... you tried calling validMoves on an empty space:" + startPosition);
        }
        TeamColor team = piece.getTeamColor();
        var moves = piece.pieceMoves(board, startPosition);
        var valid = piece.pieceMoves(board, startPosition);

        for (ChessMove move : moves) {
            if (movePutsKingInCheck(team, move)) {
                valid.remove(move);
            }
        }

        // EN PASSANT

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            ChessPosition leftPosition = new ChessPosition(startPosition.getRow(), startPosition.getColumn()-1);
            ChessPiece left = board.boundlessGetPiece(leftPosition);
            if (left != null && left.getPieceType() == PieceType.PAWN && left.getTeamColor() != piece.getTeamColor()) {
                if (lastMove.getEndPosition().equals(leftPosition)) {
                    valid.add(new ChessMove(startPosition, leftPosition, null));
                }
            }

            ChessPosition rightPosition = new ChessPosition(startPosition.getRow(), startPosition.getColumn()+1);
            ChessPiece right = board.boundlessGetPiece(rightPosition);
            if (right != null && right.getPieceType() == PieceType.PAWN && right.getTeamColor() != piece.getTeamColor()) {
                if (lastMove.getEndPosition().equals(rightPosition)) {
                    valid.add(new ChessMove(startPosition, rightPosition, null));
                }
            }
        }

        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }

        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        }

        doMove(move);
        lastMove = move;
        swapTeam();
//        enPassHandler.setLastMove(move);
    }


    /**
     * Makes a move in a chess game WITHOUT checking if it's valid
     *
     * @param move chess move to perform
     */
    private void doMove(ChessMove move) {
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        board.removePiece(move.getStartPosition());
        if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8) {
                board.addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece()));
                return;
            }
        }
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
     * Sees if a move would put king out of checkmate. Does not change board. Assumes King is already in check.
     *
     * @param teamColor the team whose king is in check
     * @param move the chess move being tested
     * @return true if performing move does get king out of check, false otherwise
     */
    private boolean moveEscapesCheck(TeamColor teamColor, ChessMove move) {
        saveBoard();

        boolean doesEscape = false;
        doMove(move);
        if (!isInCheck(teamColor)) {
            doesEscape = true;
        }

        revertBoard();
        return doesEscape;
    }

    /**
     * Sees if a move would put king into check.
     *
     * @param teamColor the team you're checking the check status for
     * @param move the chess move being tested
     * @return true if performing move puts king in check, false otherwise
     */
    private boolean movePutsKingInCheck(TeamColor teamColor, ChessMove move) {
        saveBoard();

        boolean doesPutInCheck = false;
        doMove(move);
        if (isInCheck(teamColor)) {
            doesPutInCheck = true;
        }

        revertBoard();
        return doesPutInCheck;
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
        for (var pair : saveBoardAndGetCopy()) {
            if (pair.isNull() || pair.piece().getTeamColor() != teamColor) {
                continue;
            }
            // ...look at all their moves...
            for (var move : pair.piece().pieceMoves(board, pair.position())) {
                // ...and see if one of them gets me out of checkmate.
                if (moveEscapesCheck(teamColor, move)) {
                    return false;
                }
            }
        }
        revertBoard();
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
            return false; // can't be in check to be in stalemate.
        }

        ChessPosition kingStartPosition = board.getKingPosition(teamColor);
        ChessPiece king = board.getPiece(kingStartPosition);

        // for all pieces on my team...
        for (var pair : saveBoardAndGetCopy()) {
            if (pair.isNull() || pair.piece().getTeamColor() != teamColor) {
                continue;
            }
            // ...look at all their moves...
            var teammatePieceMoves = pair.piece().pieceMoves(board, pair.position());
            for (ChessMove move : teammatePieceMoves) {
                // ...and see if any of them can get me out of check.
                if (moveEscapesCheck(teamColor, move)) {
                    return false;
                }
            }
        }
        revertBoard();
        return true;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn
            && board.equals(chessGame.board)
            && savedBoards.equals(chessGame.savedBoards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, savedBoards);
    }

    @Override
    public String toString() {
        return "ChessGame{\n" +
                board + "\n" +
                teamTurn + "'s turn\n" +
                '}';
    }
}
