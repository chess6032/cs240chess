package ui.phases;

import static chess.ChessGame.TeamColor;

import chess.ChessPosition;
import client.Client;
import client.ResponseException;
import client.ServerFacade;
import model.GameData;

import static ui.uidrawing.UIDrawer.printCommand;
import static ui.uidrawing.UIDrawer.println;

import ui.CommandAndArgs;
import ui.InvalidArgsFromUser;
import ui.ReplResult;
import ui.uidrawing.BoardDrawer;

import java.util.List;

public class GameplayUI extends UiPhase {
    private final GameData gameData;
    private final TeamColor team;

    public GameplayUI(ServerFacade server, GameData gameData, TeamColor teamColor) {
        super(List.of(
                "help",
                "redraw",
                "highlight",
                "ping",
                "leave"
        ), server);
        this.gameData = gameData;
        if (gameData.game() == null) {
            throw new RuntimeException("HEY! Don't enter Gameplay UI without a ChessGame!");
        }
        team = teamColor;
        drawBoard();
    }

    @Override
    public Runnable eval(CommandAndArgs cargs) throws InvalidArgsFromUser, ResponseException {
        return switch (cargs.command()) {
            case "help" -> this::help;
            case "redraw" -> this::drawBoard;
            case "highlight" -> highlightLegalMoves(cargs.args());
            case "ping" -> { setResult(new ReplResult(true)); yield(null); }
            case "leave" -> { setResult(new ReplResult(Client.State.POSTLOGIN)); yield(null); }
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

    private ChessPosition inputPosToChessPos(String input) {
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

    private void help() {
        printCommand("redraw");
        printCommand("highlight <position>");
        printCommand("ping");
        printCommand("leave");
    }
}
