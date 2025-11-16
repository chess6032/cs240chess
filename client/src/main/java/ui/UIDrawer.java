package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import chess.ChessBoard;

import static ui.EscapeSequences.*;

public abstract class UIDrawer {

    // out stream
    private static final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    // vars to keep track of formatting

    private static BgColor bgColor = BgColor.DEFAULT;
    private static TextColor textColor = TextColor.DEFAULT;

    public static BgColor getBgColor() {
        return bgColor;
    }
    public static TextColor getTextColor() {
        return textColor;
    }

    // WRAPPERS & HELPERS

    public static void print(Object obj) { out.print(obj); }
    public static void print(Object... params) {
        for (var param : params) {
            print(param);
        }
    }
    public static void println(Object obj) {
        print(BgColor.DEFAULT.seq());
        out.println(obj);
        print(bgColor.seq());
    }
    public static void println() {
        print(BgColor.DEFAULT.seq());
        out.println();
        print(bgColor.seq());
    }
    public static void println(Object... params) {
        print(params);
        println();
    }

    public static void moveCursor(int x, int y) { print(moveCursorToLocation(x, y)); }
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

    public static void main(String[] args) {
        eraseScreen();

        BoardDrawer.useUniPieces();
//        BoardDrawer.useAsciiPieces();

        println("Empty board:");
        var board = new ChessBoard();
        BoardDrawer.printBoard(board);
        println();

        println("Starting board:");
        board.resetBoard();
        BoardDrawer.printBoard(board);
    }

}
