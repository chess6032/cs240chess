package ui;

import client.Client;
import model.AuthData;
import model.GameData;
import model.UserData;
import chess.ChessGame.TeamColor;

public record ReplResult(Client.State state, UserData user, AuthData auth, GameData gameData, TeamColor color, boolean ping) {

    public ReplResult(Client.State state, GameData gameData, TeamColor color, boolean ping) {
        this(state, null, null, gameData, color, ping);
    }

    public ReplResult(Client.State state) {
        this(state, null, null, null, null, false);
    }

    public ReplResult(Client.State state, GameData gameData, TeamColor color) {
        this(state, null, null, gameData, color, false);
    }

    public ReplResult(Client.State state, UserData user, AuthData auth) {
        this(state, user, auth, null, null, false);
    }

    public ReplResult(boolean ping) {
        this(null, null, null, null, null, ping);
    }

}
