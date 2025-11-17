package ui.uiDrawing;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;

import java.util.HashMap;
import java.util.Map;

import static ui.uiDrawing.EscapeSequences.REGULAR_EMPTY;
import static ui.uiDrawing.EscapeSequences.WIDE_EMPTY;


public class BoardDrawer extends UIDrawer {


    // CONSTANTS

    private static final BgColor BLACK_BG = BgColor.BROWN;
    private static final BgColor WHITE_BG = BgColor.LIGHT_BROWN;

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

    // formatting vars

    public static boolean usingUniPieces;
    private static Map<ChessPiece.PieceType, Integer> pieceInts;
    private static char whiteCharStart;
    private static char blackCharStart;
    private static char emptyPieceChar;

    static {
        useAsciiPieces();
    }

    private static String boardOffset = DEFAULT_BOARD_OFFSET;

    // HELPERS

    private static void printWithOffset(Object obj) {
        print(BgColor.DEFAULT.seq());
        print(boardOffset);
        print(getBgColor().seq());
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
                print(WHITE_PIECE_CLR.seq());
            } else if (piece.getTeamColor() == TeamColor.BLACK) {
                print(BLACK_PIECE_CLR.seq());
            }
        }

        print(pieceStr(piece));
        print(getTextColor().seq()); // reset text color
    }

    private static void printRow(int row, ChessBoard board) {
        printWithOffset(" ", row+1, " ");

        int alternator = row % 2 == 0 ? 1 : 0; // used for making each row's starting color alternate
        for (int c = 0; c < ChessBoard.getBoardWidth(); ++c) {
            print(c % 2 == alternator ? WHITE_BG.seq() : BLACK_BG.seq()); // set background to appropriate grid square color
            var piece = board.getPiece(new ChessPosition(row+1, c+1));
            printPiece(piece);
        }
        print(getBgColor().seq()); // reset background

        print(" ", row+1, " ");
    }

    private static void printLettersRow() {
        printWithOffset("   ");
        for (int c = 0; c < ChessBoard.getBoardWidth(); ++c) {
            print("%c%c ".formatted(emptyPieceChar, (char) 'a' + c));
        }
        println("   ");
    }


    public static void printBoard(ChessBoard board, TeamColor viewerTeam, int boardOffsetSpaces) {
        boardOffset = " ".repeat(boardOffsetSpaces);

        var bgColorHold = getBgColor();
        setBgColor(BOARD_BG_CLR);
        var textColorHold = getTextColor();
        setTextColor(BOARD_TEXT_CLR);

        // print letters (for grid coords)
        printLettersRow();

        // print grid
        boolean blacksPerspective = viewerTeam == TeamColor.BLACK;
        for (int r = 0; r < ChessBoard.getBoardWidth(); ++r) {
            printRow(blacksPerspective ? r : ChessBoard.getBoardWidth()-1-r, board);
            println();
        }

        printLettersRow();

        setBgColor(bgColorHold);
        setTextColor(textColorHold);
    }

    public static void printBoard(ChessBoard board, TeamColor viewerTeam) {
        printBoard(board, viewerTeam, DEFAULT_BOARD_OFFSET.length());
    }
}