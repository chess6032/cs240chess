package mytests.chess;

import chess.*;
import chess.moves.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

import java.util.HashSet;

public class CkhMovesCalculatorTests {

    private static void printPossibleMoves(PieceMovesCalculator calculator) {
        calculator.calculateMoves();
        HashSet<ChessMove> possibleMoves = calculator.getPossibleMoves();
        System.out.println("Possible moves: " + possibleMoves.size());
        for (ChessMove move : possibleMoves) {
            System.out.println(move.getEndPosition());
        }
        System.out.println();
    }

    public static void testKingMovesCalculator() {
        ChessBoard board = new ChessBoard();
        ChessPosition position = new ChessPosition(4, 4);
        board.addPiece(position, new ChessPiece(TeamColor.WHITE, PieceType.KING));

        KingMovesCalculator calculator = new KingMovesCalculator(board, position, TeamColor.WHITE);
        System.out.println(board.toString());
        printPossibleMoves(calculator);

        calculator = new KingMovesCalculator(board, position, TeamColor.WHITE);
        board.addPiece(new ChessPosition(position.getRow()-1, position.getRow()-1), new ChessPiece(TeamColor.WHITE, PieceType.PAWN));
        System.out.println(board.toString());
        printPossibleMoves(calculator);

        calculator = new KingMovesCalculator(board, position, TeamColor.WHITE);
        board.addPiece(new ChessPosition(position.getRow()-1, position.getRow()-1), new ChessPiece(TeamColor.BLACK, PieceType.PAWN));
        System.out.println(board.toString());
        printPossibleMoves(calculator);

        board = new ChessBoard();
        position = new ChessPosition(1, 4);
        board.addPiece(position, new ChessPiece(TeamColor.WHITE, PieceType.KING));

        calculator = new KingMovesCalculator(board, position, TeamColor.WHITE);
        System.out.println(board.toString());
        printPossibleMoves(calculator);
    }
}
