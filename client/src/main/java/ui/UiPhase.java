package ui;

import client.Client;
import client.ServerFacade;
import model.AuthData;
import model.UserData;
import ui.uiDrawing.UIDrawer;

import java.util.*;

public abstract class UiPhase {


    private static final Scanner scanner = new Scanner(System.in);

    private final ServerFacade server;
    protected final List<String> commands;

    private Client.State clientState;
    private UserData clientUserData;

    protected void setClientState(Client.State state) {
        clientState = state;
    }
    protected void setClientUserData(UserData user) {
        clientUserData = user;
    }

    public UiPhase(List<String> commands, ServerFacade server) {
        this.commands = commands;
        this.server = server;
    }

    public ReplResult readEvalPrint() {
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
                result = "Invalid input: " + cargs.command() +
                        "\n" + e.getMessage();
            }
        } catch (UnknownCommandFromUser e) {
            result = e.getMessage();
        }

        // PRINT
        UIDrawer.println(result);

        // give client updated state (modified in eval)
        return new ReplResult(clientState, clientUserData);
    }



    protected String parseCommand(String command) throws UnknownCommandFromUser {
        // for now, this method is really simple.
        // but it can be expanded upon to allow for more flexible user input.

        if (commands.contains(command)) {
            return command;
        }

        throw new UnknownCommandFromUser(command);
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
}
