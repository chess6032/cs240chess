package ui.uiDrawing;

import static ui.uiDrawing.EscapeSequences.*;

public enum BgColor {
    BLACK(SET_BG_COLOR_BLACK),
    LIGHT_GREY(SET_BG_COLOR_LIGHT_GREY),
    DARK_GREY(SET_BG_COLOR_DARK_GREY),
    RED(SET_BG_COLOR_RED),
    GREEN(SET_BG_COLOR_GREEN),
    DARK_GREEN(SET_BG_COLOR_DARK_GREEN),
    YELLOW(SET_BG_COLOR_YELLOW),
    BLUE(SET_BG_COLOR_BLUE),
    MAGENTA(SET_BG_COLOR_MAGENTA),
    WHITE(SET_BG_COLOR_WHITE),
    DEFAULT(RESET_BG_COLOR),
    BROWN(136, 98, 62),
    LIGHT_BROWN(198, 164, 108)
    ;

    private final String escapeSequence;

    BgColor(String escapeSequence) {
        this.escapeSequence = escapeSequence;
    }

    BgColor(int r, int g, int b) {
        escapeSequence = "\u001b[48;2;%d;%d;%dm".formatted(r, g, b);
    }

    public String seq() {
        return escapeSequence;
    }
}
