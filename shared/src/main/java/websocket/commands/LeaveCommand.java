package websocket.commands;

import chess.ChessGame.TeamColor;

import java.util.Objects;

public class LeaveCommand extends UserGameCommand {

    private final TeamColor team;

    public LeaveCommand(String authToken, Integer gameID, TeamColor team) {
        super(CommandType.LEAVE, authToken, gameID);
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
        LeaveCommand that = (LeaveCommand) o;
        return team == that.team;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), team);
    }

    @Override
    public String toString() {
        return "LeaveCommand{" +
                "team=" + team +
                '}';
    }
}
