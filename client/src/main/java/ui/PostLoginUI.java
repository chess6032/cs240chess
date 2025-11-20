package ui;

import client.Client;
import client.ResponseException;
import client.ServerFacade;
import ui.uiDrawing.EscapeSequences;
import ui.uiDrawing.TextColor;

import java.util.List;

import static ui.uiDrawing.UIDrawer.*;

public class PostLoginUI extends UiPhase {

    private final String username;

    public PostLoginUI(ServerFacade server, String username) {
        super(List.of(
            "help",
            "create",
            "list",
            "join",
            "observe",
            "logout"
        ), server);
        assert(username != null);
        this.username = username;
    }

    @Override
    public Runnable eval(CommandAndArgs cargs) throws InvalidArgsFromUser, ResponseException {
        return switch (cargs.command()) {
            case "help" -> this::help;
            case "create" -> createGame(cargs.args());
            case "list" -> listGames(cargs.args());
            case "join" -> joinGame(cargs.args());
            case "observe" -> observeGame(cargs.args());
            case "logout" -> this::logout;
            default -> {
                setResult(new ReplResult(Client.State.EXIT));
                yield() -> println("Sorry, I...sharted my pants. " + cargs.command());
            }
        };
    }

    private void logout() {
        setResult(new ReplResult(Client.State.PRELOGIN));
        print(EscapeSequences.SET_TEXT_ITALIC);
        println("Logging out...");
        print(EscapeSequences.RESET_TEXT_ITALIC);
    }

    private Runnable observeGame(String[] args) {
        return null;
    }

    private Runnable joinGame(String[] args) {
        return null;
    }

    private Runnable listGames(String[] args) {
        return null;
    }

    private Runnable createGame(String[] args) {
        return null;
    }

    private void help() {
        println("Signed in as ", username);

        printCommand("create <name>");
        printCommand("list");
        printCommand("join <id>");
        printCommand("observe <id>");
        printCommand("logout");
    }
}
