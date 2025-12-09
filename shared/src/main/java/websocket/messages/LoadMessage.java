package websocket.messages;

import model.GameData;

public class LoadMessage extends ServerMessage {
    private final GameData gameData;
    private final chess.ChessGame game;

    public LoadMessage(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        this.gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), null);
        this.game = gameData.game();
    }
}
