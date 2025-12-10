package websocket.commands;

import chess.ChessGame.TeamColor;

import java.util.Objects;

public class ConnectCommand extends UserGameCommand {
    public ConnectCommand(String authToken, Integer gameID, TeamColor team) {
        super(CommandType.CONNECT, authToken, gameID);
    }

    @Override
    public String toString() {
        return "ConnectCommand{" + super.toString(false) + "}";
    }
}
