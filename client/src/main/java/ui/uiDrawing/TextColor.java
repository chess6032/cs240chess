package ui.uiDrawing;

import static ui.uiDrawing.EscapeSequences.*;

public enum TextColor {
    BLACK(SET_TEXT_COLOR_BLACK),
    LIGHT_GREY(SET_TEXT_COLOR_LIGHT_GREY),
    DARK_GREY(SET_TEXT_COLOR_DARK_GREY),
    RED(SET_TEXT_COLOR_RED),
    GREEN(SET_TEXT_COLOR_GREEN),
    YELLOW(SET_TEXT_COLOR_YELLOW),
    BLUE(SET_TEXT_COLOR_BLUE),
    MAGENTA(SET_TEXT_COLOR_MAGENTA),
    WHITE(SET_TEXT_COLOR_WHITE),
    DEFAULT(RESET_TEXT_COLOR),
    DARK_BROWN(71, 46, 25),
    LIGHT_BROWN(255, 209, 135)
    ;

    private final String escapeSequence;

    TextColor(String escapeSequence) {
        this.escapeSequence = escapeSequence;
    }

    TextColor(int r, int g, int b) {
        escapeSequence = "\u001b[38;2;%d;%d;%dm".formatted(r, g, b);
    }

    public String seq() {
        return escapeSequence;
    }
}
