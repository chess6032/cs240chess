package ui.uidrawing;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ui.uidrawing.EscapeSequences.REGULAR_EMPTY;
import static ui.uidrawing.EscapeSequences.WIDE_EMPTY;


public class BoardDrawer extends UIDrawer {


    // CONSTANTS

    private static final BgColor DARK_SQUARE_BG = BgColor.BROWN;
    private static final BgColor LIGHT_SQUARE_BG = BgColor.LIGHT_BROWN;

    private static final BgColor HIGHLIGHTED_LIGHT_SQUARE_BG = BgColor.DARK_GREEN;
    private static final BgColor HIGHLIGHTED_DARK_SQUARE_BG = BgColor.GREEN;
    private static final BgColor HIGHLIGHTED_BLACK_PIECE_BG = BgColor.WHITE;
    private static final BgColor HIGHLIGHTED_WHITE_PIECE_BG = BgColor.BLACK;

    private static final TextColor WHITE_PIECE_CLR = TextColor.WHITE;
    private static final TextColor BLACK_PIECE_CLR = TextColor.BLACK;

    private static final BgColor BOARD_BG_CLR = BgColor.DARK_GREY;
    private static final TextColor BOARD_TEXT_CLR = TextColor.LIGHT_GREY;

    private static final char WHITE_UNI_START = '♔';
    private static final char BLACK_UNI_START = '♚';
    private static final char WHITE_ASCII_START = 'K';
    private static final char BLACK_ASCII_START = 'k';

    private static Map<ChessPiece.PieceType, Integer> uniChessPieceComparisons() {
        HashMap<ChessPiece.PieceType, Integer> map = new HashMap<>();
        map.put(ChessPiece.PieceType.KING, 0);
        map.put(ChessPiece.PieceType.QUEEN, 1);
        map.put(ChessPiece.PieceType.ROOK, 2);
        map.put(ChessPiece.PieceType.BISHOP, 3);
        map.put(ChessPiece.PieceType.KNIGHT, 4);
        map.put(ChessPiece.PieceType.PAWN, 5);
        return map;
    }

    private static Map<ChessPiece.PieceType, Integer> asciiChessPieceComparisons() {
        HashMap<ChessPiece.PieceType, Integer> map = new HashMap<>();
        map.put(ChessPiece.PieceType.KING, 0);    // K = 75
        map.put(ChessPiece.PieceType.QUEEN, 6);   // Q = 81
        map.put(ChessPiece.PieceType.ROOK, 7);    // R = 82
        map.put(ChessPiece.PieceType.BISHOP, -9); // B = 66
        map.put(ChessPiece.PieceType.KNIGHT, 3);  // N = 78
        map.put(ChessPiece.PieceType.PAWN, 5);    // P = 80
        return map;
    }

    private static final String DEFAULT_BOARD_OFFSET = "          ";

    // FORMATTING VARS

    public static boolean usingUniPieces;
    private static Map<ChessPiece.PieceType, Integer> pieceInts;
    private static char whiteCharStart;
    private static char blackCharStart;
    private static char emptyPieceChar;

    static {
        useAsciiPieces();
    }

    private static String boardOffset = DEFAULT_BOARD_OFFSET;

    // OTHER VARS

    private static Collection<ChessPosition> highlightedEndPositions = null;
    private static ChessPosition highlightedPiecePosition = null;

    // HELPERS

    private static void printWithOffset(Object obj) {
//        print(BgColor.DEFAULT.seq());
        useBgColor(BgColor.DEFAULT);
        print(boardOffset);
        revertBgColor();
        print(obj);
    }
    private static void printWithOffset(Object... params) {
        printWithOffset("");
        print(params);
    }

    public static void useUniPieces() {
        pieceInts = uniChessPieceComparisons();
        whiteCharStart = WHITE_UNI_START;
        blackCharStart = BLACK_UNI_START;
        emptyPieceChar = WIDE_EMPTY;
        usingUniPieces = true;
    }
    public static void useAsciiPieces() {
        pieceInts = asciiChessPieceComparisons();
        whiteCharStart = WHITE_ASCII_START;
        blackCharStart = BLACK_ASCII_START;
        emptyPieceChar = REGULAR_EMPTY;
        usingUniPieces = false;
    }

    private static String pieceStr(ChessPiece piece) {
        if (piece == null) {
            return " %c ".formatted(emptyPieceChar);
        }

        var team = piece.getTeamColor();
        var type = piece.getPieceType();

        char start;
        if (team == TeamColor.WHITE) {
            start = whiteCharStart;
        } else if (team == TeamColor.BLACK){
            start = blackCharStart;
        } else {
            return " ☹ ";
        }

        return " %c ".formatted((char) (start + pieceInts.get(type)));
    }

    private static void printPiece(ChessPiece piece) {
        // set text color to black/white, corresponding to piece's team color
        if (piece != null) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                useTextColor(WHITE_PIECE_CLR);
            } else if (piece.getTeamColor() == TeamColor.BLACK) {
                useTextColor(BLACK_PIECE_CLR);
            }
        }

        print(pieceStr(piece));
//        print(getTextColor().seq()); // reset text color
        revertTextColor(); // reset text color
    }

    private static void printRow(int row, ChessBoard board, boolean blacksPerspective) {
        printWithOffset(" ", row+1, " ");

        int alternator = row % 2 == 0 ? 1 : 0; // used for making each row's starting color alternate
        for (int i = 0; i < ChessBoard.getBoardWidth(); ++i) {
            int c = blacksPerspective ? ChessBoard.getBoardWidth() - 1 - i : i;
            var position = new ChessPosition(row+1, c+1);
            boolean isLightSquare = c % 2 == alternator;

            // set color of square
            useBgColor(isLightSquare ? LIGHT_SQUARE_BG : DARK_SQUARE_BG); // set background to appropriate grid square color
            if (highlightedEndPositions != null) {
                if (highlightedEndPositions.contains(position)) {
                    useBgColor(isLightSquare ? HIGHLIGHTED_DARK_SQUARE_BG : HIGHLIGHTED_LIGHT_SQUARE_BG);
                }
                if (position.equals(highlightedPiecePosition)) {
                    var piece = board.getPiece(position);
                    if (piece != null) {
                        useBgColor(piece.getTeamColor() == TeamColor.WHITE ? HIGHLIGHTED_WHITE_PIECE_BG : HIGHLIGHTED_BLACK_PIECE_BG);
                    }
                }
            }

            var piece = board.getPiece(position);
            printPiece(piece);
        }
        revertBgColor(); // reset background

        print(" ", row+1, " ");
    }

    private static void printLettersRow(boolean blacksPerspective) {
        printWithOffset("   ");
        for (int i = 0; i < ChessBoard.getBoardWidth(); ++i) {
            char c = (char) ('a' + (char) (blacksPerspective ? (ChessBoard.getBoardWidth()-1-i) : i));
            print("%c%c ".formatted(emptyPieceChar, c));
        }
        println("   ");
    }


    public static void printBoard(ChessBoard board, TeamColor viewerTeam) {
        boardOffset = DEFAULT_BOARD_OFFSET;

        var bgColorHold = getBgColor();
        setPersistingBgColor(BOARD_BG_CLR);
        var textColorHold = getTextColor();
        setPersistingTextColor(BOARD_TEXT_CLR);

        boolean blacksPerspective = viewerTeam == TeamColor.BLACK;

        // print letters (for grid coords)
        printLettersRow(blacksPerspective);

        // print grid
        for (int r = 0; r < ChessBoard.getBoardWidth(); ++r) {
            printRow(blacksPerspective ? r : ChessBoard.getBoardWidth()-1-r, board, blacksPerspective);
            println();
        }

        printLettersRow(blacksPerspective);

        setPersistingBgColor(bgColorHold);
        setPersistingTextColor(textColorHold);
    }


    public static void highlightMoves(ChessBoard board, TeamColor viewerTeam, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece == null) {
            return;
        }

        highlightedPiecePosition = position;
        highlightedEndPositions = piece.pieceMovesEndPositions(board, position);
        printBoard(board, viewerTeam);
        highlightedEndPositions = null;
        highlightedPiecePosition = null;
    }
}