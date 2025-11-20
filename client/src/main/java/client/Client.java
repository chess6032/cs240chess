package client;

import static client.Client.State.*;
import static ui.uiDrawing.UIDrawer.*;

import model.AuthData;
import ui.PostLoginUI;
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
            if (result == null) {
                continue;
            }

            var resultUser = result.user();
            var resultAuth = result.auth();

            if (resultUser != null) {
//                assert(username == null);
                assert(state == PRELOGIN);
                username = resultUser.username();
            }
            if (resultAuth != null) {
//                assert(authToken == null);
                assert(state == PRELOGIN);
                authToken = resultAuth.authToken();
            }

            var newState = result.state();

            if (newState == PRELOGIN) {
                if (!phase.getClass().equals(PreLoginUI.class)) {
                    phase = new PreLoginUI(server);
                }
            } else if (newState == POSTLOGIN) {
                if (!phase.getClass().equals(PostLoginUI.class)) {
                    phase = new PostLoginUI(server, username);
                }
            } else if (newState == GAMEPLAY) {

            }

            state = newState;
        }
    }
}
