package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import chess.ChessBoard;
import static chess.ChessPiece.PieceType;
import static chess.ChessGame.TeamColor;

import static ui.EscapeSequences.*;

public class UIDrawer {

    // out stream
    private static final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    // CONSTANTS

    private static final BgColor BLACK_BG = BgColor.DARK_GREY;
    private static final BgColor WHITE_BG = BgColor.LIGHT_GREY;

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

    // vars & constants to keep track of formatting

    private static BgColor bgColor = BgColor.DEFAULT;
    private static String empty = REGULAR_EMPTY;

    private static Map<PieceType, Integer> pieceInts = asciiChessPieceComparisons();
    private static char whiteCharStart = WHITE_ASCII_START;
    private static char blackCharStart = BLACK_ASCII_START;

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
    private static void printEmpty() { print(empty); }
    private static void printEmpty(int n) {
        for (int i = 0; i < n; ++i) {
            printEmpty();
        }
    }
    private static void moveCursor(int x, int y) { print(moveCursorToLocation(x, y)); }
    private static void eraseScreen() {
        print(ERASE_SCREEN);
    }
    private static void resetFormatting() {
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

    private static void setBgColor(BgColor color) {
        bgColor = color;
        print(color.seq());
    }
    private static void resetBgColor() {
        setBgColor(BgColor.DEFAULT);
    }

    private static void setTextColor(TextColor color) {
        print(color.seq());
    }
    private static void resetTextColor() {
        setTextColor(TextColor.DEFAULT);
    }

    private static void useWideEmpty() {
        empty = WIDE_EMPTY;
    }
    private static void useRegularEmpty() {
        empty = REGULAR_EMPTY;
    }

    public static void main(String[] args) {
        eraseScreen();

        for (var key : pieceInts.keySet()) {
            println(key, ": ", pieceStr(TeamColor.WHITE, key));
        }

        println();

        for (var key : pieceInts.keySet()) {
            println(key, ": ", pieceStr(TeamColor.BLACK, key));
        }

//        println("Empty board:");
//        var board = new ChessBoard();
//        printBoard(board);
//        println();
//
//        println("Starting board:");
//        board.resetBoard();
//        printBoard(board);
    }


    private static String pieceStr(TeamColor team, PieceType type) {
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

    private static void printRow() {
        for (int i = 0; i < ChessBoard.getBoardWidth(); ++i) {
            setBgColor(i % 2 == 0 ? WHITE_BG : BLACK_BG);

        }
    }

    public static void printBoard(ChessBoard board) {

    }
}
