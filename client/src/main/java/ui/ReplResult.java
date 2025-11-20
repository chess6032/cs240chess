package ui;

import client.Client;
import model.AuthData;
import model.UserData;

public record ReplResult(Client.State state, UserData user, AuthData auth) {}
