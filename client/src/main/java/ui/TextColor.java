package ui;

import static ui.EscapeSequences.*;

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
    DEFAULT(RESET_TEXT_COLOR)
    ;

    private final String escapeSequence;

    TextColor(String escapeSequence) {
        this.escapeSequence = escapeSequence;
    }

    public String seq() {
        return escapeSequence;
    }
}
