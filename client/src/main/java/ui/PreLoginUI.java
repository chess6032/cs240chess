package ui;

import client.Client;
import client.ResponseException;
import client.ServerFacade;
import model.AuthData;
import model.UserData;

import java.util.List;

import static server.HttpResponseCodes.*;

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
    public String eval(CommandAndArgs cargs) throws InvalidArgsFromUser, ResponseException {
        return switch (cargs.command()) {
            case "help" -> help();
            case "register" -> register(cargs.args());
            case "login" -> login(cargs.args());
            case "quit" -> quit();
            default -> {
                setResult(new ReplResult(Client.State.EXIT));
                yield "Sorry, I...pooped my pants. " + cargs.command();
            }
        };
    }

    private String help() {
        return """
                You are not logged in.
                
                register <username> <password> <email>
                login <username> <password>
                quit
                """;
    }

    private String register(String[] args) throws InvalidArgsFromUser, ResponseException {
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
            return "That username is already taken.";
        }

        setResult(new ReplResult(Client.State.POSTLOGIN, user, auth));
        return "Welcome, " + user.username();
    }

    private String login(String[] args) throws InvalidArgsFromUser, ResponseException {
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
            return "Username or password incorrect.";
        }

        setResult(new ReplResult(Client.State.POSTLOGIN, user, auth));
        return "Logged in as " + user.username();
    }

    private String quit() {
        setResult(new ReplResult(Client.State.EXIT));
        return "Exiting chess...";
    }
}
