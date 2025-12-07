package ui.phases;

import client.Client;
import client.ResponseException;
import client.ServerFacade;
import model.AuthData;
import model.UserData;

import java.util.List;

import static server.HttpResponseCodes.*;
import static ui.uidrawing.UIDrawer.*;

import ui.CommandAndArgs;
import ui.InvalidArgsFromUser;
import ui.ReplResult;
import ui.uidrawing.EscapeSequences;

public class PreLoginUI extends UiPhase{
    public PreLoginUI(ServerFacade server) {
        super(List.of(
            "help",
            "register",
            "login",
            "quit"
        ), server);
    }

    @Override
    public Runnable eval(CommandAndArgs cargs) throws InvalidArgsFromUser, ResponseException {
        return switch (cargs.command()) {
            case "help" -> this::help;
            case "register" -> register(cargs.args());
            case "login" -> login(cargs.args());
            case "quit" -> this::quit;
            default -> {
                setResult(new ReplResult(Client.State.EXIT));
                yield () -> println("Sorry, I...pooped my pants. " + cargs.command());
            }
        };
    }

    private void help() {
        println("You are not logged in. Login/register to play!");

        printCommand("register <username> <password> <email>");
        printCommand("login <username> <password>");
        printCommand("quit");
    }

    private Runnable register(String[] args) throws InvalidArgsFromUser, ResponseException {
        if (args.length != 3) {
            throw new InvalidArgsFromUser(
                    "register <username> <password> <email>",
                    "register mario128 MarioBR0S! mario@superbrosplumbing.com");
        }

        UserData user = new UserData(args[0], args[1], args[2]);
        AuthData auth = null;

        try {
            auth = server.register(user);
        } catch (ResponseException e) {
            if (e.getStatus() != ALREADY_TAKEN_STATUS) {
                throw e;
            }
            return () -> println("That username is already taken.");
        }

        setResult(new ReplResult(Client.State.POSTLOGIN, user, auth));
        return () -> println("Welcome, " + user.username());
    }

    private Runnable login(String[] args) throws InvalidArgsFromUser, ResponseException {
        if (args.length != 2) {
            throw new InvalidArgsFromUser("login <username> <password",
                    "login mario128 MarioBR0S!");
        }

        UserData user = new UserData(args[0], args[1], null);
        AuthData auth = null;

        try {
            auth = server.login(user);
        } catch (ResponseException e) {
            if (e.getStatus() != UNAUTHORIZED_STATUS) {
                throw e;
            }
            return () -> println("Username or password incorrect.");
        }

        setResult(new ReplResult(Client.State.POSTLOGIN, user, auth));
        return () -> println("Logged in as " + user.username());
    }

    private void quit() {
        setResult(new ReplResult(Client.State.EXIT));
        print(EscapeSequences.SET_TEXT_ITALIC);
        println("Exiting chess...");
        print(EscapeSequences.RESET_TEXT_ITALIC);
    }
}
