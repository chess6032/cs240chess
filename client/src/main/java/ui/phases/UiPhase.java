package ui.phases;

import client.ResponseException;
import client.ServerFacade;
import ui.CommandAndArgs;
import ui.InvalidArgsFromUser;
import ui.ReplResult;
import ui.UnknownCommandFromUser;
import ui.uidrawing.TextColor;
import ui.uidrawing.UIDrawer;

import java.util.*;

public abstract class UiPhase {


    private static final Scanner SCANNER = new Scanner(System.in);

    protected final ServerFacade server;
    protected final List<String> commands;

    private ReplResult replResult;

    protected UiPhase(List<String> commands, ServerFacade server) {
        this.commands = commands;
        this.server = server;
        replResult = null;
    }

    protected void setResult(ReplResult result) {
        replResult = result;
    }

    // TODO: refactor InvalidArgsFromUser exception and then use only this overload.
    protected void validateInput(String[] args, int argsCount) throws InvalidArgsFromUser {
        if (args == null) {
            if (argsCount != 0) {
                throw new InvalidArgsFromUser();
            }
        } else if (args.length != argsCount) {
            throw new InvalidArgsFromUser();
        }
    }

    protected void validateInput(String[] args, int[] argsCounts) throws InvalidArgsFromUser {
        if (args == null) {
            if (argsCounts != null) {
                throw new InvalidArgsFromUser();
            }
        } else {
            for (var count : argsCounts) {
                if (args.length == count) {
                    return;
                }
            }
            throw new InvalidArgsFromUser();
        }
    }

    protected void validateInput(String[] args, int argsCount, String commandFormat, String exampleCommand) throws InvalidArgsFromUser {
        if (args == null) {
            if (argsCount != 0) {
                throw new InvalidArgsFromUser(commandFormat, exampleCommand);
            }
        } else if (args.length != argsCount) {
            throw new InvalidArgsFromUser(commandFormat, exampleCommand);
        }
    }

    public ReplResult readEvalPrint() {
        // single iteration of the REP loop

        UIDrawer.printPrompt();
        Runnable printFunc = null;
        try {
            // READ
            String line = SCANNER.nextLine();
            var cargs = parseInput(line);
            try {
                // EVAL
                printFunc = eval(cargs);
            } catch (InvalidArgsFromUser e) {
                printFunc = () -> {
                    UIDrawer.useTextColor(TextColor.RED);
                    UIDrawer.println("Invalid input");
                    UIDrawer.revertTextColor();
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
