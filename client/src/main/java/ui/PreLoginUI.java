package ui;

import java.util.ArrayList;
import java.util.List;

public class PreLoginUI {
    private final List<String> commands = new ArrayList<>();

    {
        var commandNames = new String[]{
                "Help",
                "Register",
                "Login",
                "Quit"};
        for (int i = 0; i < commandNames.length; ++i) {
            commands.add(Integer.toString(i+1) + ". " + commandNames[i]);
        }
    }



}
