package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import static chess.ChessPiece.PieceType;
import static chess.ChessGame.TeamColor;

import static ui.EscapeSequences.*;

public class UIDrawer {

    // out stream
    private static final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    // CONSTANTS

    private static final BgColor BLACK_BG = BgColor.BROWN;
    private static final BgColor WHITE_BG = BgColor.LIGHT_BROWN;

    private static final TextColor WHITE_PIECE_CLR = TextColor.WHITE;
    private static final TextColor BLACK_PIECE_CLR = TextColor.BLACK;

    private static final BgColor BOARD_BG_CLR = BgColor.DARK_GREY;
    private static final TextColor BOARD_TEXT_CLR = TextColor.LIGHT_GREY;

    private static final char WHITE_UNI_START = '\u2654';
    private static final char BLACK_UNI_START = '\u265A';
    private static final char WHITE_ASCII_START = 'k';
    private static final char BLACK_ASCII_START = 'K';

    private static Map<PieceType, Integer> uniChessPieceComparisons() {
        HashMap<PieceType, Integer> map = new HashMap<>();
        map.put(PieceType.KING, 0);
        map.put(PieceType.QUEEN, 1);
        map.put(PieceType.ROOK, 2);
        map.put(PieceType.BISHOP, 3);
        map.put(PieceType.KNIGHT, 4);
        map.put(PieceType.PAWN, 5);
        return map;
    }

    private static Map<PieceType, Integer> asciiChessPieceComparisons() {
        HashMap<PieceType, Integer> map = new HashMap<>();
        map.put(PieceType.KING, 0);    // K = 75
        map.put(PieceType.QUEEN, 6);   // Q = 81
        map.put(PieceType.ROOK, 7);    // R = 82
        map.put(PieceType.BISHOP, -9); // B = 66
        map.put(PieceType.KNIGHT, 3);  // N = 78
        map.put(PieceType.PAWN, 5);    // P = 80
        return map;
    }

    private static String BOARD_OFFSET = "          ";

    // vars to keep track of formatting

    private static BgColor bgColor = BgColor.DEFAULT;
    private static TextColor textColor = TextColor.DEFAULT;

    public static boolean usingUniPieces = true;
    private static Map<PieceType, Integer> pieceInts = uniChessPieceComparisons();
    private static char whiteCharStart = BLACK_UNI_START;
    private static char blackCharStart = BLACK_UNI_START;
    private static char emptyPieceChar = WIDE_EMPTY;

    // WRAPPERS & HELPERS

    private static void print(Object obj) { out.print(obj); }
    private static void print(Object... params) {
        for (var param : params) {
            print(param);
        }
    }
    private static void println(Object obj) {
        print(BgColor.DEFAULT.seq());
        out.println(obj);
        print(bgColor.seq());
    }
    private static void println() {
        print(BgColor.DEFAULT.seq());
        out.println();
        print(bgColor.seq());
    }
    private static void println(Object... params) {
        print(params);
        println();
    }
    private static void printWithOffset(Object obj) {
        print(BgColor.DEFAULT.seq());
        print(BOARD_OFFSET);
        print(bgColor.seq());
        print(obj);
    }
    private static void printWithOffset(Object... params) {
        printWithOffset("");
        print(params);
    }
    private static void moveCursor(int x, int y) { print(moveCursorToLocation(x, y)); }
    public static void eraseScreen() {
        print(ERASE_SCREEN);
    }
    public static void resetFormatting() {
        resetBgColor();
        resetTextColor();

        String[] resets = {
                RESET_TEXT_BOLD_FAINT,
                RESET_TEXT_ITALIC,
                RESET_TEXT_UNDERLINE,
                RESET_TEXT_BLINKING,
        };
        for (var reset : resets) {
            print(reset);
        }
    }

    public static void setBgColor(BgColor color) {
        bgColor = color;
        print(color.seq());
    }
    public static void resetBgColor() {
        setBgColor(BgColor.DEFAULT);
    }

    public static void setTextColor(TextColor color) {
        textColor = color;
        print(color.seq());
    }
    public static void resetTextColor() {
        setTextColor(TextColor.DEFAULT);
    }

    public static void useUniPieces() {
        pieceInts = uniChessPieceComparisons();
        whiteCharStart = BLACK_UNI_START;
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

    public static void main(String[] args) {
        eraseScreen();

        useUniPieces();
//        useAsciiPieces();

        println("Empty board:");
        var board = new ChessBoard();
        printBoard(board);
        println();

        println("Starting board:");
        board.resetBoard();
        printBoard(board);
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
                print(WHITE_PIECE_CLR.seq());
            } else if (piece.getTeamColor() == TeamColor.BLACK) {
                print(BLACK_PIECE_CLR.seq());
            }
        }

        print(pieceStr(piece));
        print(textColor.seq()); // reset text color
    }

    private static void printRow(int row, ChessBoard board) {
        printWithOffset(" ", row+1, " ");

        int alternator = row % 2 == 0 ? 0 : 1; // used for making each row's starting color alternate
        for (int c = 0; c < ChessBoard.getBoardWidth(); ++c) {
            print(c % 2 == alternator ? WHITE_BG.seq() : BLACK_BG.seq()); // set background to appropriate grid square color
            var piece = board.getPiece(new ChessPosition(row+1, c+1));
            printPiece(piece);
        }
        print(bgColor.seq()); // reset background

        print(" ", row+1, " ");
    }

    private static void printLettersRow() {
        printWithOffset("   ");
        for (int c = 0; c < ChessBoard.getBoardWidth(); ++c) {
            print("%c%c ".formatted(emptyPieceChar, (char) 'a' + c));
        }
        print("   ");
    }


    public static void printBoard(ChessBoard board) {
        var bgColorHold = bgColor;
        setBgColor(BOARD_BG_CLR);
        var textColorHold = textColor;
        setTextColor(BOARD_TEXT_CLR);

        // print letters (for grid coords)
        printLettersRow();
        println();

        // print grid
        for (int r = 0; r < ChessBoard.getBoardWidth(); ++r) {
            printRow(r, board);
            println();
        }

        printLettersRow();

        setBgColor(bgColorHold);
        setTextColor(textColorHold);
    }
}
