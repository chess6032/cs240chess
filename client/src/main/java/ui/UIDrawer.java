package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class UIDrawer {

    private static final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    // Vars to keep track of formatting

    private static BgColor bgColor = BgColor.DEFAULT;

    // CONSTANTS

    private static final String EMPTY = REGULAR_EMPTY;

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
    private static void printEmpty() { print(EMPTY); }
    private static void printEmpty(int n) {
        for (int i = 0; i < n; ++i) {
            printEmpty();
        }
    }
    private static void moveCursor(int x, int y) { print(moveCursorToLocation(x, y)); }
    private static void eraseScreen() {
        print(ERASE_SCREEN);
    }
    private static void eraseLine()   { print(ERASE_LINE); }
    private static void resetFormatting() {
        resetBgColor();
        resetTextColor();

        String[] resets = {
                RESET_TEXT_BOLD_FAINT,
                RESET_TEXT_ITALIC,
                RESET_TEXT_UNDERLINE,
                RESET_TEXT_BLINKING,
//                RESET_TEXT_COLOR,
//                RESET_BG_COLOR,
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

    public static void main(String[] args) {
        eraseScreen();

        for (int i = 0; i < 8; ++i) {
            setBgColor(i % 2 == 0 ?
                BgColor.RED : BgColor.BLUE);
            printEmpty();
        }

        println();
        printEmpty(5);
        println();
        println();
        printEmpty();
        setTextColor(TextColor.BLACK);
        print("Hello");
        resetFormatting();
        print("hello again");
    }


}
