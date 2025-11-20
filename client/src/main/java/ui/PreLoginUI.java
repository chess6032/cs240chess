package ui;

import client.Client;
import client.ResponseException;
import client.ServerFacade;
import model.AuthData;
import model.UserData;

import java.util.List;

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
    public String eval(CommandAndArgs cargs) throws InvalidArgsFromUser {
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

    private String register(String[] args) throws InvalidArgsFromUser {
        if (args.length < 3) {
            throw new InvalidArgsFromUser("register <username> <password> <email>",
                    "register mario128 MarioBR0S! mario@superbrosplumbing.com");
        }

        UserData user = new UserData(args[0], args[1], args[2]);
        AuthData auth = null;
        try {
            auth = server.register(user);
        } catch (ResponseException e) {
            
        }

        setResult(new ReplResult(Client.State.POSTLOGIN, user, auth));
        return "Registered new user: " + user.username();
    }

    private String login(String[] args) throws InvalidArgsFromUser {
        if (args.length < 2) {
            throw new InvalidArgsFromUser("login <username> <password",
                    "login mario128 MarioBR0S!");
        }

        UserData user = new UserData(args[0], args[1], null);
        AuthData auth = server.login(user);

        setResult(new ReplResult(Client.State.POSTLOGIN, user, auth));
        return "Logged in as " + user.username();
    }

    private String quit() {
        setResult(new ReplResult(Client.State.EXIT));
        return "Exiting chess...";
    }
}
