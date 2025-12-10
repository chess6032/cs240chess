package websocket.commands;

import chess.ChessGame.TeamColor;

import java.util.Objects;

public class LeaveCommand extends UserGameCommand {

    public LeaveCommand(String authToken, Integer gameID) {
        super(CommandType.LEAVE, authToken, gameID);
    }

    @Override
    public String toString() {
        return "LeaveCommand{" + super.toString(false) + "}";
    }
}
