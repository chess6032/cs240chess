package websocket.messages;

import chess.ChessGame;
import model.GameData;

import java.util.Objects;

public class LoadMessage extends ServerMessage {
    private final GameData gameMeta;
    private final chess.ChessGame game;

    public LoadMessage(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        this.gameMeta = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), null);
        this.game = gameData.game();
    }

    public GameData getGameMeta() {
        return gameMeta;
    }

    public ChessGame getChessGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        LoadMessage that = (LoadMessage) o;
        return Objects.equals(gameMeta, that.gameMeta) && Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameMeta, game);
    }

    @Override
    public String toString() {
        return "LoadMessage{" +
                "gameMeta=" + gameMeta +
                ", game=" + game +
                '}';
    }
}
