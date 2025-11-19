package ui;

public class UnknownCommandFromUser extends Exception {
    public UnknownCommandFromUser(String badCommand) {
        /**
         * Encourages user to type "help" for list of commands
         *
         * @param badCommand the command the user gave. (i.e. the first word they typed into the terminal.)
         */
        super("Unknown command: " + badCommand
            + ". \n Type help for list of commands.");
    }
}
