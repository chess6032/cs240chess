package client;

import static client.Client.State.*;
import static ui.uiDrawing.UIDrawer.*;

import model.AuthData;
import model.GameData;
import model.UserData;
import ui.uiDrawing.BoardDrawer;

import java.util.Scanner;

public class Client {
    private final BoardDrawer boardDrawer = new BoardDrawer();
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade server;

    private static final String INTRO_MESSAGE = "♕ 240 Chess Client";

    private State state = PRELOGIN;
    private UserData user;
    private AuthData auth;

    public enum State {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY,
        EXIT
    }

    public Client(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public void run() {
        println(INTRO_MESSAGE);

        while (state != EXIT) {
            // stub
            state = EXIT;
        }
    }
}
