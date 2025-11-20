package client;

import static client.Client.State.*;
import static ui.uiDrawing.UIDrawer.*;

import ui.PreLoginUI;
import ui.ReplResult;
import ui.UiPhase;

public class Client {
    private final ServerFacade server;

    private static final String INTRO_MESSAGE = "♕ 240 Chess Client";

    private State state;
    private String username;
    private String authToken;

    private UiPhase phase;

    public enum State {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY,
        EXIT
    }

    public Client(String serverURL) {
        server = new ServerFacade(serverURL);

        state = PRELOGIN;
        phase = new PreLoginUI(server);
    }

    public void run() {
        println(INTRO_MESSAGE);

        while (state != EXIT) {
            ReplResult result = phase.readEvalPrint();
            transition(result);
        }
    }

    private void transition(ReplResult result) {

//        // SHOULD NEVER GET HERE
//        UIDrawer.println("Sorry! Something went wrong trying to transition between application states.",
//                "(%s to %s)".formatted(state, newState),
//                "Exiting...");
    }
}
