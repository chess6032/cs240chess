package websocket.commands;

import chess.ChessGame.TeamColor;

import java.util.Objects;

public class ResignCommand extends UserGameCommand {

    public ResignCommand(String authToken, Integer gameID, TeamColor team) {
        super(CommandType.RESIGN, authToken, gameID);
    }

    @Override
    public String toString() {
        return "ResignCommand{}";
    }
}
