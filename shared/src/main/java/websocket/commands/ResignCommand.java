package websocket.commands;

import chess.ChessGame.TeamColor;

import java.util.Objects;

public class ResignCommand extends UserGameCommand {

    private final TeamColor team;

    public ResignCommand(String authToken, Integer gameID, TeamColor team) {
        super(CommandType.RESIGN, authToken, gameID);
        this.team = team;
    }

    public TeamColor getTeam() {
        return team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ResignCommand that = (ResignCommand) o;
        return team == that.team;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), team);
    }

    @Override
    public String toString() {
        return "ResignCommand{" +
                "team=" + team +
                '}';
    }
}
