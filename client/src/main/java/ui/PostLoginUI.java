package ui;

import client.ResponseException;
import client.ServerFacade;

import java.util.List;

public class PostLoginUI extends UiPhase {
    protected PostLoginUI(List<String> commands, ServerFacade server) {
        super(List.of(

        ), server);
    }

    @Override
    public Runnable eval(CommandAndArgs cargs) throws InvalidArgsFromUser, ResponseException {
        return () -> {};
    }
}
