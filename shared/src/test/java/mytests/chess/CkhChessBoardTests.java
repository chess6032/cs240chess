package mytests.chess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;
import chess.moves.*;


public class CkhChessBoardTests {

    public static void testChessBoardConstructor() {
        ChessBoard board = new ChessBoard();
        System.out.println(board.toString());
    }

    public static void testResetBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.println(board.toString());
    }

    public static void testDisplayingMoves() {
        ChessBoard board = new ChessBoard();
        ChessPiece piece = new ChessPiece(TeamColor.BLACK, PieceType.KING);
        ChessPosition position = new ChessPosition(4, 4);
        board.addPiece(position, piece);
        KingMovesCalculator calculator = new KingMovesCalculator(board, position, TeamColor.BLACK);
        calculator.calculateMoves();
        System.out.println(board.toString(KingMovesCalculator.getFinalPositions(calculator.getPossibleMoves())));

        System.out.println();
        System.out.println();

        board.addPiece(new ChessPosition(3, 5), new ChessPiece(TeamColor.BLACK, PieceType.PAWN));
        board.addPiece(new ChessPosition(3, 3), new ChessPiece(TeamColor.WHITE, PieceType.PAWN));
        System.out.println(board);
        System.out.println();

        calculator = new KingMovesCalculator(board, position, TeamColor.BLACK);
        calculator.calculateMoves();
        System.out.println(board.toString(KingMovesCalculator.getFinalPositions(calculator.getPossibleMoves())));

        System.out.println();
        System.out.println();

        board = new ChessBoard();
        position = new ChessPosition(1, 1);
        board.addPiece(position, piece);
        calculator = new KingMovesCalculator(board, position, TeamColor.WHITE);
        calculator.calculateMoves();
        System.out.println(board.toString(KingMovesCalculator.getFinalPositions(calculator.getPossibleMoves())));
    }
}
