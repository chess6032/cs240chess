package ui;

public class InvalidArgsFromUser extends Exception {

    private final String format;
    private final String example;

    /**
     * Contains the correct formatting for the command the user tried to use.
     * (Does not include the line the user type, only what it should have looked like.)
     *
     * @param commandFormat the args format for the command the user inputted.
     */
    public InvalidArgsFromUser(String commandFormat, String exampleCommand) {
        super("format: " + commandFormat + " | example: " + exampleCommand);
        format = commandFormat;
        example = exampleCommand;
    }

    public InvalidArgsFromUser(String message) {
        super(message);
        format = null;
        example = null;
    }

    public String getFormat() {
        return format;
    }

    public String getExample() {
        return example;
    }
}
