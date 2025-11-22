package ui;

import client.Client;
import model.AuthData;
import model.UserData;
import chess.ChessGame.TeamColor;

public record ReplResult(Client.State state, UserData user, AuthData auth, Integer gameID, TeamColor color) {

    public ReplResult(Client.State state) {
        this(state, null, null);
    }

    public ReplResult(Client.State state, UserData user, AuthData auth) {
        this(state, user, auth, null, null);
    }

    public ReplResult(Client.State state, int gameID, TeamColor color) {
        this(state, null, null, gameID, color);
    }
}
