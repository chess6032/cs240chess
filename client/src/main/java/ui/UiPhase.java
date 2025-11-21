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

    protected void validateInput(String[] args, int argsCount, String commandFormat, String exampleCommand) throws InvalidArgsFromUser {
        if (args.length != argsCount) {
            throw new InvalidArgsFromUser(commandFormat, exampleCommand);
        }
    }

    protected UiPhase(List<String> commands, ServerFacade server) {
        this.commands = commands;
        this.server = server;
        replResult = null;
    }

    public ReplResult readEvalPrint() {
        // single iteration of the REP loop

        UIDrawer.printPrompt();
        Runnable printFunc = null;
        try {
            // READ
            String line = scanner.nextLine();
            var cargs = parseInput(line);
            try {
                // EVAL
                printFunc = eval(cargs);
            } catch (InvalidArgsFromUser e) {
                printFunc = () -> {
                    UIDrawer.println("Invalid input");
                    UIDrawer.println(cargs.command(), " should look like this: ");
                    UIDrawer.println("   ", e.getFormat());
                    UIDrawer.println("for example:");
                    UIDrawer.println("   ", e.getExample());
                    UIDrawer.println();
                    UIDrawer.println("Type help for a list of commands");
                };
            } catch (ResponseException e) {
                // TODO: wtf do I do here
                printFunc = () -> UIDrawer.print((e.getStatus() / 100 == 5 ? "[Server error]" : "[User error]")
                        + " Sorry! Something went wrong...");
            }
        } catch (UnknownCommandFromUser e) {
            printFunc = () -> UIDrawer.println(e.getMessage());
        }

        // PRINT
        if (printFunc != null) {
            printFunc.run();
        }

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

    public abstract Runnable eval(CommandAndArgs cargs) throws InvalidArgsFromUser, ResponseException;
}
