package ui;

import client.ResponseException;
import client.ServerFacade;
import ui.uiDrawing.UIDrawer;

import java.util.*;

public abstract class UiPhase {


    private static final Scanner scanner = new Scanner(System.in);

    protected final ServerFacade server;
    protected final List<String> commands;

//    private Client.State clientState;
//    private UserData clientUserData;
//    private AuthData clientAuthData;

//    protected void setResultState(Client.State state) {
//        clientState = state;
//    }
//    protected void setResultUserData(UserData user) {
//        clientUserData = user;
//    }
//    protected void setResultAuthData(AuthData auth) {
//        clientAuthData = auth;
//    }

    private ReplResult replResult;

    protected void setResult(ReplResult result) {
        replResult = result;
    }

    protected UiPhase(List<String> commands, ServerFacade server) {
        this.commands = commands;
        this.server = server;
        replResult = null;
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
//            } catch (ResponseException e) {
//                // TODO: wtf do I do here
//                UIDrawer.print((e.getCode() == ResponseException.Code.ServerError ? "[Server error]" : "[User error]")
//                        + " Sorry! Something went wrong. Please try again.");
//            }
        } catch (UnknownCommandFromUser e) {
            result = e.getMessage();
        }

        // PRINT
        UIDrawer.println(result);

        // give client updated state (modified in eval)
        return replResult;
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
