package client;

import static client.Client.State.*;
import static ui.uiDrawing.UIDrawer.*;

import model.AuthData;
import model.UserData;
import ui.PreLoginUI;
import ui.ReplResult;
import ui.UiPhase;
import ui.uiDrawing.BoardDrawer;

import java.util.Scanner;

public class Client {
    private final ServerFacade server;

    private static final String INTRO_MESSAGE = "♕ 240 Chess Client";

    private State state;
    private UserData user;
    private AuthData auth;

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
        phase = new PreLoginUI();
    }

    public void run() {
        println(INTRO_MESSAGE);

        while (state != EXIT) {
            // stub

            var result = phase.readEvalPrint();
            transitionState(result);
        }
    }

    private void transitionState(ReplResult result) {

    }
}
