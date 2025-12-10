package ui.phases;

import static chess.ChessGame.TeamColor;
import static ui.uidrawing.UIDrawer.*;

import chess.ChessMove;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;
import client.Client;
import client.ResponseException;
import client.ServerFacade;
import model.GameData;

import java.util.List;

import ui.CommandAndArgs;
import ui.InvalidArgsFromUser;
import ui.ReplResult;
import ui.uidrawing.BoardDrawer;
import ui.uidrawing.TextColor;
import websocket.messages.ErrorServerMessage;
import websocket.messages.NotificationMessage;

public class GameplayUI extends UiPhase {
    private final GameData gameData;
    private final TeamColor team;

    private ChessMove move = null;

    public GameplayUI(ServerFacade server, GameData gameData, TeamColor teamColor) {
        super(List.of(
                "help",
                "redraw",
                "highlight",
                "move",
                "resign",
                "leave"
        ), server);
        this.gameData = gameData;
        if (gameData.game() == null) {
            throw new RuntimeException("HEY! Don't enter Gameplay UI without a ChessGame!");
        }
        team = teamColor;
//        drawBoard();
    }

    public ChessMove getMove() {
        return move;
    }

    @Override
    public Runnable eval(CommandAndArgs cargs) throws InvalidArgsFromUser, ResponseException {
        return switch (cargs.command()) {
            case "help" -> this::help;
            case "redraw" -> this::drawBoard;
            case "highlight" -> highlightLegalMoves(cargs.args());
            case "move" -> updateMove(cargs.args());
            case "resign" -> resign();
            case "leave" -> this::leave;
            default -> {
                setResult(new ReplResult(Client.State.EXIT));
                yield() -> println("Erm... what the sigma??");
            }
        };
    }

    private Runnable highlightLegalMoves(String[] args) throws InvalidArgsFromUser {
        validateInput(args, 1);

        ChessPosition position = inputPosToChessPos(args[0]);
        if (position == null) {
            throw new InvalidArgsFromUser("incorrect input format for position.");
        }

        if (gameData.game().getBoard().getPiece(position) == null) {
            throw new InvalidArgsFromUser("there is no piece at that position.");
        }

        return () -> BoardDrawer.highlightMoves(gameData.game().getBoard(), team, position);
    }

    private void drawBoard() {
        BoardDrawer.printBoard(gameData.game().getBoard(), team);
    }

    private static ChessPosition inputPosToChessPos(String input) {
        if (input == null || input.length() != 2) {
            return null;
        }

        char colChar = input.charAt(0);
        if (colChar < 'a' || colChar > 'h') {
            return null;
        }
        int col = colChar - 'a' + 1;

        char rowChar = input.charAt(1);
        if (rowChar < '1' || rowChar > '8') {
            return null;
        }
        int row = rowChar - '1' + 1;

        return new ChessPosition(row, col);
    }

    private static String chessPosToString(ChessPosition position) {
        if (position == null) {
            return null;
        }

        char row = (char) ('0' + position.getRow());
        char col = (char) ('a' + position.getColumn() - 1);
        return "" + col + row;
    }

    private static String chessMoveToString(ChessMove move) {
        if (move == null) {
            return null;
        }
        if (move.getEndPosition() == null || move.getStartPosition() == null) {
            return null;
        }

        String start = chessPosToString(move.getStartPosition());
        String end = chessPosToString(move.getEndPosition());

        String str = start + " -> " + end;
        if (move.getPromotionPiece() != null) {
            str += " (promoted to " + move.getPromotionPiece() + ")";
        }

        return str;
    }

    public static PieceType parsePromotionInput(String input) {
        return switch (input) {
            case "q" -> PieceType.QUEEN;
            case "r" -> PieceType.ROOK;
            case "n" -> PieceType.KNIGHT;
            case "b" -> PieceType.BISHOP;
            default -> null;
        };
    }

    private void help() {
        printCommand("help");
        printCommand("redraw");
        printCommand("highlight <position>");
        if (!amObserving()) { printCommand("move <start position> <end position> <pawn promotion>"); }
        if (!amObserving()) { printCommand("resign"); }
        printCommand("leave");
    }

    private Runnable resign() throws InvalidArgsFromUser {
        if (amObserving()) {
            throw new InvalidArgsFromUser();
            // FIXME: this will print "Invalid Input", whereas all other invalid commands print "Can't find command:"
            //  ...but this is just the quickest, easiest way I could think to implement this.
        }
        setResult(new ReplResult(Client.State.POSTLOGIN));
        return () -> {
            println("Coward!");
        };
    }

    private Runnable updateMove(String[] args) throws InvalidArgsFromUser {
        if (amObserving()) {
            throw new InvalidArgsFromUser();
            // FIXME: same thing as in resign
        }

        validateInput(args, new int[]{2, 3});
        if (gameData.game().getTeamTurn() != team) {
            return () -> {
                println("It's not your turn! Wait for the other player to make their move.");
            };
        }

        // parse user input
        var startPos = inputPosToChessPos(args[0]);
        var endPos = inputPosToChessPos(args[1]);
        // verify parse was successful
        if (startPos == null || endPos == null) {
            throw new InvalidArgsFromUser("GameplayUI.updateMove: bad input for positions");
        }

        return () -> {
            print("Making move: ");
            useTextColor(TextColor.BLUE);
            print(chessPosToString(move.getStartPosition()), " -> ", chessPosToString(move.getEndPosition()));
            revertTextColor();
            println();
        };
    }

    private void leave() {
        setResult(new ReplResult(Client.State.POSTLOGIN));
        printlnItalics("Leaving game...");
    }

    private boolean amObserving() {
        return team == null;
    }

    public static void printWsError(ErrorServerMessage err) {
        useTextColor(TextColor.RED);
        println(err.getErrorMessage());
        revertTextColor();
    }

    public static Runnable evaluateWsNotifPrint(NotificationMessage notif) {
        String username = notif.getInfo().username();
        TeamColor team = notif.getInfo().team();
        ChessMove move = notif.getInfo().move();

        String msg = switch (notif.getType()) {
            case PLAYER_JOINED -> username + " joined game as " + team;
            case OBSERVER_JOINED -> username + " is watching you...";
            case PLAYER_MADE_MOVE -> username + " (" + team + ") made move: " + chessMoveToString(move);
            case CLIENT_LEFT -> username + " is no longer with us :(";
            case PLAYER_RESIGNED -> username + " gave up!";
            case PLAYER_IN_CHECK -> username + " is in check";
            case PLAYER_IN_CHECKMATE -> username + " is in checkmate";
            case STALE_MATE -> username + " is in stalemate";
        };

        return () -> {
            useTextColor(TextColor.YELLOW);
            println();
            println(msg);
            revertTextColor();
        };
    }

}
