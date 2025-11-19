package ui;

public class InvalidArgsFromUser extends RuntimeException {
    /**
     * Contains the correct formatting for the command the user tried to use.
     * (Does not include the line the user type, only what it should have looked like.)
     *
     * @param commandFormat the args format for the command the user inputted.
     */
    public InvalidArgsFromUser(String commandFormat) {
        super(commandFormat);
    }
}
