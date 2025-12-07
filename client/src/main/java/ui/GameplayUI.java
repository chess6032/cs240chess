package ui;

import chess.ChessGame;
import static chess.ChessGame.TeamColor;
import client.Client;
import client.ResponseException;
import client.ServerFacade;
import model.GameData;

import java.util.List;

import static ui.uidrawing.UIDrawer.printCommand;
import static ui.uidrawing.UIDrawer.println;

import ui.uidrawing.BoardDrawer;

public class GameplayUI extends UiPhase {
    private final GameData gameData;
    private final TeamColor team;

    public GameplayUI(ServerFacade server, GameData gameData, TeamColor teamColor) {
        super(List.of(
                "help",
                "redraw",
                "quit"
        ), server);
        this.gameData = gameData;
        if (gameData.game() == null) {
            throw new RuntimeException("HEY! Don't enter Gameplay UI without a ChessGame!");
        }
        team = teamColor;
    }

    @Override
    public Runnable eval(CommandAndArgs cargs) throws InvalidArgsFromUser, ResponseException {
        return switch (cargs.command()) {
            case "help" -> this::help;
            case "redraw" -> this::drawBoard;
            case "quit" -> { setResult(new ReplResult(Client.State.POSTLOGIN)); yield(null); }
            default -> {
                setResult(new ReplResult(Client.State.EXIT));
                yield() -> println("Erm... what the sigma??");
            }
        };
    }

    private void drawBoard() {
        BoardDrawer.printBoard(gameData.game().getBoard(), team);
    }

    private void help() {
        printCommand("redraw");
        printCommand("quit");
    }
}
