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
    public String eval(CommandAndArgs cargs) throws InvalidArgsFromUser, ResponseException {
        return switch (cargs.command()) {
            case "help" -> help();
            case "register" -> register(cargs.args());
            case "login" -> login(cargs.args());
            case "quit" -> quit();
            default -> {
                setResultState(Client.State.EXIT);
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
        if (args.length < 3) {
            throw new InvalidArgsFromUser("register <username> <password> <email>",
                    "register mario128 MarioBR0S! mario@superbrosplumbing.com");
        }

        UserData user = new UserData(args[0], args[1], args[2]);
        AuthData auth = server.register(user);

        setResultUserData(user);
        setResultAuthData(auth);
        setResultState(Client.State.POSTLOGIN);
        return "Registered new user: " + user.username();
    }

    private String login(String[] args) throws InvalidArgsFromUser, ResponseException {
        if (args.length < 2) {
            throw new InvalidArgsFromUser("login <username> <password",
                    "login mario128 MarioBR0S!");
        }

        UserData user = new UserData(args[0], args[1], null);
        AuthData auth = server.login(user);

        setResultUserData(user);
        setResultState(Client.State.POSTLOGIN);

        return "Logged in as " + user.username();
    }

    private String quit() {
        setResultState(Client.State.EXIT);
        return "Exiting chess...";
    }
}
