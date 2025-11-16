package ui;

import static ui.EscapeSequences.*;

public enum BgColor {
    BLACK(SET_BG_COLOR_BLACK),
    LIGHT_GREY(SET_BG_COLOR_LIGHT_GREY),
    DARK_GREY(SET_BG_COLOR_DARK_GREY),
    RED(SET_BG_COLOR_RED),
    GREEN(SET_BG_COLOR_GREEN),
    YELLOW(SET_BG_COLOR_YELLOW),
    BLUE(SET_BG_COLOR_BLUE),
    MAGENTA(SET_BG_COLOR_MAGENTA),
    WHITE(SET_BG_COLOR_WHITE),
    DEFAULT(RESET_BG_COLOR)
    ;

    private final String escapeSequence;

    BgColor(String escapeSequence) {
        this.escapeSequence = escapeSequence;
    }

    public String seq() {
        return escapeSequence;
    }
}
