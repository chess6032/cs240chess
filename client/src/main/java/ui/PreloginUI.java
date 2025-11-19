package ui;

import client.Client;

import java.util.List;

import java.util.ArrayList;

public class PreloginUI extends UiPhase{
    public PreloginUI() {
        super(List.of(
            "help",
            "login",
            "register",
            "quit"
        ));
        setClientState(Client.State.PRELOGIN);
    }

    @Override
    public String eval(CommandAndArgs cargs) throws InvalidArgsFromUser {
        return "";
    }
}
