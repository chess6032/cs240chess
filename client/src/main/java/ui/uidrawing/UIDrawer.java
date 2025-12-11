package ui.uidrawing;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import chess.ChessBoard;
import chess.ChessGame;

import static ui.uidrawing.EscapeSequences.*;

public abstract class UIDrawer {

    // out stream
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

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

    public static void print(Object obj) { OUT.print(obj); }
    public static void print(Object... params) {
        for (var param : params) {
            print(param);
        }
    }
    public static void println(Object obj) {
        print(obj);
        println();
    }
    public static void println() {
        useBgColor(BgColor.DEFAULT);
        OUT.println();
        revertBgColor();
    }
    public static void println(Object... params) {
        print(params);
        println();
    }

    public static void printlnItalics(Object obj) {
        print(EscapeSequences.SET_TEXT_ITALIC);
        println(obj);
        print(EscapeSequences.RESET_TEXT_ITALIC);
    }

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

    public static void setPersistingBgColor(BgColor color) {
        bgColor = color;
        print(color.seq());
    }
    public static void resetBgColor() {
        setPersistingBgColor(BgColor.DEFAULT);
    }

    public static void setPersistingTextColor(TextColor color) {
        textColor = color;
        print(color.seq());
    }
    public static void resetTextColor() {
        setPersistingTextColor(TextColor.DEFAULT);
    }

    public static void useTextColor(TextColor color) {
        // DON'T FORGET TO REVERT
        print(color.seq());
    }
    public static void revertTextColor() {
        print(textColor.seq());
    }
    public static void useBgColor(BgColor color) {
        // DON'T FORGET TO REVERT
        print(color.seq());
    }
    public static void revertBgColor() {
        print(bgColor.seq());
    }

    // METHODS USED BY UI CLASSES

    public static void printPrompt() {
        printPrompt(null);
    }
    public static void printPrompt(String prefix) {
        useTextColor(TextColor.GREEN);
        if (prefix != null) { print(prefix); }
        print(" >>> ");
        revertTextColor();
    }

    public static void printCommand(String line) {
        String[] tokens = line.split("\\s+");
        if (line == null || line.isEmpty()) {
            return;
        }

        var textColorHold = textColor;
        setPersistingTextColor(TextColor.BLUE);

        useTextColor(TextColor.YELLOW);
        print(tokens[0], " ");
        revertTextColor();

        for (int i = 1; i < tokens.length; ++i) {
            print(tokens[i], " ");
        }
        println();

        setPersistingTextColor(textColorHold);
    }

    // main method for testing

    public static void main(String[] args) {
        eraseScreen();

        if (args.length > 0) {
            String arg = args[0];
            if (arg.equals("uni")) {
                BoardDrawer.useUniPieces();
            }
        }

        var board = new ChessBoard();
        board.resetBoard();

        println("Starting board, from white's perspective:");
        BoardDrawer.printBoard(board, ChessGame.TeamColor.WHITE);
        println();

        println(board);
        println();

        println("Starting board, from black's perspective:");
        BoardDrawer.printBoard(board, ChessGame.TeamColor.BLACK);
    }

}
