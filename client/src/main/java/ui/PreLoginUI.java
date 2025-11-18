package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ui.uiDrawing.UIDrawer.*;

public class PreLoginUI {
    private static final List<String> commands = new ArrayList<>();

    static {
        var commandNames = new String[]{
                "Help",      // 1.
                "Register",  // 2.
                "Login",     // 3.
                "Quit"       // 4.
        };
        for (int i = 0; i < commandNames.length; ++i) {
            commands.add(Integer.toString(i+1) + ". " + commandNames[i]);
        }
    }

    private final Scanner scanner = new Scanner(System.in);

    // UTILITY METHODS

    private String removeEndCharsIfPresent(String s, char[] chars) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        for (char ch : chars) {
            if (s.charAt(s.length()-1) == ch) {
                return s.substring(0, s.length()-2);
            }
        }

        return s;
    }

    private int parseCommand(String arg) {
        if (arg == null || arg.isEmpty()) { // FIXME: is \ n included at the end of input scan ?
            return -1;
        }

        String command = removeEndCharsIfPresent(arg, new char[]{'.', ')'});
        int commandCode;
        try {
            commandCode = Integer.parseInt(command);
            if (commandCode > commands.size()-1) {
                commandCode = -1;
            }
        } catch (NumberFormatException e) {
            commandCode = commands.indexOf(command);
        }

        return commandCode;
    }
    
    // REPL METHODS
    
    private void read() {
        printPrompt("[LOGGED OUT]");
        String line = scanner.nextLine();
        String[] args = line.split("\\s+"); // split input into words
        int commandCode = parseCommand(args[0]);
    }

    private String eval(String line) {
        return null;
    }

    public static void main(String[] args) {
        var ui = new PreLoginUI();

        String[] tests = {"Help"};
        for (var str : tests) {
            int code = ui.parseCommand(str);
            if (code == -1) {
                System.out.println(code);
            } else {
                System.out.println(commands.get(code));
            }
        }

    }
}
