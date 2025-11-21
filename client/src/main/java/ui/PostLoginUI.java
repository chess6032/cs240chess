package ui;

import client.Client;
import client.ResponseException;
import client.ServerFacade;
import model.AuthData;
import model.GameData;
import ui.uiDrawing.EscapeSequences;
import ui.uiDrawing.TextColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static server.HttpResponseCodes.*;

import static ui.uiDrawing.UIDrawer.*;

public class PostLoginUI extends UiPhase {

    private final TextColor ID_COLOR = TextColor.YELLOW;
    private final TextColor GAMENAME_COLOR = TextColor.DEFAULT;

    private final AuthData auth;

    private ArrayList<GameData> gamesInDB; // for keeping track of which IDs displayed to user correlate to which games in database.

    private void correlateGames(Collection<GameData> games) {
        gamesInDB.clear();
        gamesInDB.addAll(games);
    }

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
        gamesInDB = new ArrayList<>();
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

    private Runnable createGame(String[] args) throws InvalidArgsFromUser, ResponseException {
        // does NOT join the user to the game, only creates it.

        validateInput(args, 1, "create <name>", "create letsBrawl");
        String gameName = args[0];

        GameData game;
        try {
            game = server.createGame(auth, gameName);
        } catch (ResponseException e) {
            if (e.getStatus() == UNAUTHORIZED_STATUS) {
                return this::unauthorized;
            }
            throw e;
        }



        // no setResult because state hasn't changed
        return () -> {
            gamesInDB.add(game);

            useTextColor(GAMENAME_COLOR);
            print(gameName);
            revertTextColor();
            print(" created with ID ");
            useTextColor(ID_COLOR);
            println(gamesInDB.size());
            revertTextColor();
        };
    }

    private int idxOfGameID(int gameIdInDB) {
        for (int i = 0; i < gamesInDB.size(); ++i) {
            if (gamesInDB.get(i).gameID() == gameIdInDB) {
                return i;
            }
        }
        return -1;
    }

    private Runnable listGames(String[] args) throws InvalidArgsFromUser, ResponseException {
        validateInput(args, 0, null, null);

        Collection<GameData> games;
        try {
            games = server.listGames(auth).games();
        } catch (ResponseException e) {
            if (e.getStatus() == UNAUTHORIZED_STATUS) {
                return this::unauthorized;
            }
            throw e;
        }

        correlateGames(games);

        return this::printGames;
    }

    private void printGames() {
        if (gamesInDB == null || gamesInDB.isEmpty()) {
            println("There are no active games.");
        }

        TextColor hold = getTextColor();
        setPersistingTextColor(TextColor.LIGHT_GREY);
        for (int i = 0; i < gamesInDB.size(); ++i) {
            var game = gamesInDB.get(i);

            useTextColor(TextColor.YELLOW);
            print(i+1);
            useTextColor(GAMENAME_COLOR);
            println(" ", game.gameName());
            revertTextColor();

            if (game.whiteUsername() != null) {
                println("    WHITE: ", game.whiteUsername());
            }
            if (game.blackUsername() != null) {
                println("    BLACK: ", game.blackUsername());
            }
        }
        setPersistingTextColor(hold);
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

    private void unauthorized() {
        useTextColor(TextColor.RED);
        println("Error: Unauthorized");
        revertTextColor();
        println("Something went wrong. It appears you aren't signed in.");
        println("To sign in again, use the logout command to go to the register/login page.");
    }
}
