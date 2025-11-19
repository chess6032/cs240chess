package ui;

import client.Client;
import model.AuthData;
import model.UserData;
import ui.uiDrawing.UIDrawer;

import java.util.Arrays;
import java.util.Scanner;

public abstract class UiSuper {

    private static final Scanner scanner = new Scanner(System.in);

    private Client.State clientState;
    private UserData clientUserData;
    private AuthData clientAuthData;

    protected void setClientState(Client.State state) {
        clientState = state;
    }
    protected void setClientUserData(UserData user) {
        clientUserData = user;
    }
    protected void setClientAuthData(AuthData auth) {
        clientAuthData = auth;
    }

    ReplResult readEvalPrint() {
        // single iteration of the REP loop

        UIDrawer.printPrompt();
        var result = "";
        try {
            // READ
            String line = scanner.nextLine();
            var cargs = parseInput(line);
            try {
                // EVAL
                result = eval(cargs);
            } catch (InvalidArgsFromUser e) {
                result = "Invalid input for " + cargs.command() + '\n' +
                        "Expected format: " + e.getMessage();
            }
        } catch (UnknownCommandFromUser e) {
            result = e.getMessage();
        }

        // PRINT
        UIDrawer.println(result);

        // give client updated state (modified in eval)
        return new ReplResult(clientState, clientUserData, clientAuthData);
    }

    private CommandAndArgs parseInput(String line) throws UnknownCommandFromUser {
        if (line == null || line.isEmpty()) {
            return null;
        }
        String[] tokens = line.split("\\s+"); // split along whitespace
        var command = parseCommand(tokens[0]);
        var args = Arrays.copyOfRange(tokens, 1, tokens.length);
        return new CommandAndArgs(command, args);
    }

    public abstract String eval(CommandAndArgs cargs) throws InvalidArgsFromUser;
    protected abstract String parseCommand(String command) throws UnknownCommandFromUser;
}
