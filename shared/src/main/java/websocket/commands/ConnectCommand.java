package websocket.commands;

import chess.ChessGame.TeamColor;

import java.util.Objects;

public class ConnectCommand extends UserGameCommand {
    private final TeamColor team; // null if observing

    public ConnectCommand(String authToken, Integer gameID, TeamColor team) {
        super(CommandType.CONNECT, authToken, gameID);
        this.team = team;
    }

    public TeamColor getTeam() {
        return team;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        if (!super.equals(o)) {
//            return false;
//        }
//        ConnectCommand that = (ConnectCommand) o;
//        return team == that.team;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(super.hashCode(), team);
//    }
//
//    @Override
//    public String toString() {
//        return "ConnectCommand{" +
//                "team=" + team +
//                '}';
//    }
}
