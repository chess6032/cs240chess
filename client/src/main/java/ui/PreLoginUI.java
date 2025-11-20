package ui;

import client.Client;
import client.ResponseException;
import client.ServerFacade;
import model.AuthData;
import model.UserData;

import java.util.List;

import static server.HttpResponseCodes.*;
import ui.uiDrawing.UIDrawer;
import static ui.uiDrawing.UIDrawer.println;

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
            case "help" -> help();
            case "register" -> register(cargs.args());
            case "login" -> login(cargs.args());
            case "quit" -> quit();
            default -> {
                setResult(new ReplResult(Client.State.EXIT));
                yield () -> println("Sorry, I...pooped my pants. " + cargs.command());
            }
        };
    }

    private Runnable help() {
        return () -> {
            println("""
                    You are not logged in.
                    
                    register <username> <password> <email>
                    login <username> <password>
                    quit
                    """);
        };

    }

    private Runnable register(String[] args) throws InvalidArgsFromUser, ResponseException {
        if (args.length != 3) {
            throw new InvalidArgsFromUser("register <username> <password> <email>",
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
            server.login(user);
        } catch (ResponseException e) {
            if (e.getStatus() != UNAUTHORIZED_STATUS) {
                throw e;
            }
            return () -> println("Username or password incorrect.");
        }

        setResult(new ReplResult(Client.State.POSTLOGIN, user, auth));
        return () -> println("Logged in as " + user.username());
    }

    private Runnable quit() {
        setResult(new ReplResult(Client.State.EXIT));
        return () -> println("Exiting chess...");
    }
}
