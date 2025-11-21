package ui;

import client.Client;
import client.ResponseException;
import client.ServerFacade;
import model.AuthData;
import ui.uiDrawing.EscapeSequences;
import ui.uiDrawing.TextColor;

import java.util.List;

import static server.HttpResponseCodes.*;

import static ui.uiDrawing.UIDrawer.*;

public class PostLoginUI extends UiPhase {

    private final AuthData auth;

    public PostLoginUI(ServerFacade server, AuthData auth) {
        super(List.of(
            "help",
            "create",
            "list",
            "join",
            "observe",
            "logout"
        ), server);
        if (auth == null || auth.username() == null || auth.authToken() == null) {
            throw new IllegalArgumentException("PostLoginUI: inputted auth must NOT be null, nor have any null members");
        }
        this.auth = auth;
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

    private Runnable createGame(String[] args) throws InvalidArgsFromUser {
        // does NOT join the user to the game, only creates it.

        validateInput(args, 1, "create <name>", "create letsBrawl");

        int gameID;
        try {
            gameID = server.createGame(auth, args[0]).gameID();
        } catch (ResponseException e) {
            if (e.getStatus() == UNAUTHORIZED_STATUS)
            throw new RuntimeException(e);
        }
        return null;
    }

    private Runnable listGames(String[] args) {
        return null;
    }

    private Runnable joinGame(String[] args) {
        return null;
    }

    private Runnable observeGame(String[] args) {
        return null;
    }

    private void help() {
        println("Signed in as ", auth.username());

        printCommand("create <name>");
        printCommand("list");
        printCommand("join <id> [WHITE|BLACK]");
        printCommand("observe <id>");
        printCommand("logout");
    }
}
