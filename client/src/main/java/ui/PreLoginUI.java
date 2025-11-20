package ui;

import client.Client;
import model.UserData;

import java.util.List;

public class PreLoginUI extends UiPhase{
    public PreLoginUI() {
        super(List.of(
            "help",
            "register",
            "login",
            "quit"
        ));
        setClientState(Client.State.PRELOGIN);
    }

    @Override
    public String eval(CommandAndArgs cargs) throws InvalidArgsFromUser {
        return switch (cargs.command()) {
            case "help" -> help();
            case "register" -> register(cargs.args());
            case "login" -> login(cargs.args());
            case "quit" -> quit();
            default -> {
                setClientState(Client.State.EXIT);
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

        String username = args[0];
        setClientUserData(new UserData(username, args[1], args[2]));

        setClientState(Client.State.POSTLOGIN);

        return "Registered new user: " + username;
    }

    private String login(String[] args) throws InvalidArgsFromUser {
        if (args.length < 2) {
            throw new InvalidArgsFromUser("login <username> <password",
                    "login mario128 MarioBR0S!");
        }

        String username = args[0];
        setClientUserData(new UserData(username, args[1], null));

        setClientState(Client.State.POSTLOGIN);

        return "Logged in as " + username;
    }

    private String quit() {
        setClientState(Client.State.EXIT);
        return "Exiting chess...";
    }
}
