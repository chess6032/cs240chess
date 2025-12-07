package ui;

import client.Client;
import model.AuthData;
import model.GameData;
import model.UserData;
import chess.ChessGame.TeamColor;

public record ReplResult(Client.State state, UserData user, AuthData auth, GameData gameData, TeamColor color) {

    public ReplResult(Client.State state) {
        this(state, (UserData) null, null);
    }

    public ReplResult(Client.State state, UserData user, AuthData auth) {
        this(state, user, auth, null, null);
    }

    public ReplResult(Client.State state, GameData gameData, TeamColor color) {
        this(state, null, null, gameData, color);
    }
}
