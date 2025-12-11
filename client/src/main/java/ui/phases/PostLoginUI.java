package ui.phases;

import chess.ChessGame;
import client.Client;
import client.ResponseException;
import client.ServerFacade;
import model.AuthData;
import model.GameData;
import model.PlayerColorGameIDforJSON;
import ui.CommandAndArgs;
import ui.InvalidArgsFromUser;
import ui.ReplResult;
import ui.uidrawing.EscapeSequences;
import ui.uidrawing.TextColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static server.HttpResponseCodes.*;

import static ui.uidrawing.UIDrawer.*;

public class PostLoginUI extends UiPhase {

    private static final TextColor ID_COLOR = TextColor.YELLOW;
    private static final TextColor GAMENAME_COLOR = TextColor.DEFAULT;

    private final AuthData auth;

    private final ArrayList<GameData> gamesInDB; // for keeping track of which IDs displayed to user correlate to which games in database.

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
        try {
            listGames();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Runnable phaseEval(CommandAndArgs cargs) throws InvalidArgsFromUser, ResponseException {
        return switch (cargs.command()) {
            case "help" -> this::help;
            case "create" -> createGame(cargs.args());
            case "list" -> {
                validateInput(cargs.args(), 0);
                yield listGames();
            }
            case "join" -> joinGame(cargs.args());
            case "observe" -> observeGame(cargs.args());
            case "logout" -> logout();
            default -> {
                setResult(new ReplResult(Client.State.EXIT));
                yield() -> println("Sorry, I...sharted my pants. " + cargs.command());
            }
        };
    }

    private Runnable logout() throws ResponseException {

        server.logout(auth);
        setResult(new ReplResult(Client.State.PRELOGIN));

        return () -> {
//            print(EscapeSequences.SET_TEXT_ITALIC);
//            println("Logging out...");
//            print(EscapeSequences.RESET_TEXT_ITALIC);
            printlnItalics("Logging out...");
        };
    }

    private Runnable createGame(String[] args) throws InvalidArgsFromUser, ResponseException {
        // does NOT join the user to the game, only creates it.

        validateInput(args, 1, "create <name>", "create letsBrawl");
        String gameName = args[0];

        GameData gameData;
        try {
            gameData = server.createGame(auth, gameName);
        } catch (ResponseException e) {
            if (e.getStatus() == UNAUTHORIZED_STATUS) {
                return this::unauthorized;
            }
            throw e;
        }

        listGames(); // regenerates list of games. (correlateGames)

        // no setResult because state hasn't changed
        return () -> {
            useTextColor(GAMENAME_COLOR);
            print(gameName);
            revertTextColor();
            print(" created");
            println();
        };
    }

    private Runnable listGames() throws ResponseException {

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
            noneActive();
            return;
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

    private Runnable joinGame(String[] args) throws InvalidArgsFromUser, ResponseException {
        validateInput(args, 2);

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new InvalidArgsFromUser("Please give an integer game ID.");
        }

        if (gamesInDB.isEmpty()) {
            return this::noneActive;
        }

        var game = getGameByListID(id);
        int gameIdInDB = game.gameID();

        String playerColor = args[1];
        try {
            server.joinGame(auth, new PlayerColorGameIDforJSON(playerColor, gameIdInDB));
        } catch (ResponseException e) {
            if (e.getStatus() == BAD_REQUEST_STATUS) {
                return () -> println("Player color must either be WHITE or BLACK");
            } else if (e.getStatus() == UNAUTHORIZED_STATUS) {
                return () -> println("Game not found.");
            } else if (e.getStatus() == ALREADY_TAKEN_STATUS) {
                return () -> println("That color is already taken.");
            }
            throw e;
        }

        chess.ChessGame.TeamColor color = (playerColor == null ? null :
                (playerColor.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK));

        setResult(new ReplResult(Client.State.GAMEPLAY, game, color));
        return () ->
            println("Joined ", game.gameName(), " as ", color);
    }

    private Runnable observeGame(String[] args) throws InvalidArgsFromUser {
        validateInput(args, 1);

        if (gamesInDB.isEmpty()) {
            return this::noneActive;
        }

        GameData game;
        try {
            game = getGameByListID(Integer.parseInt(args[0]));
        } catch (NumberFormatException e) {
            throw new InvalidArgsFromUser("Please enter an integer");
        }

        setResult(new ReplResult(Client.State.GAMEPLAY, game, null));

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

    private void noneActive() {
        println("There are no active games.");
    }

    private GameData getGameByListID(int id) throws InvalidArgsFromUser {
        if (id < 1) {
            throw new InvalidArgsFromUser("Game IDs start at 1.");
        }
        if (id > gamesInDB.size()) {
            throw new InvalidArgsFromUser("There are only " + Integer.toString(gamesInDB.size()) + " games.");
        }
        return gamesInDB.get(id-1);
    }
}