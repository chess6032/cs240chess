package chess.model.http;

import chess.model.GameData;

public record GameInfo(int gameID,
                       String whiteUsername, String blackUsername,
                       String gameName) {

    public GameInfo(GameData game) {
        this(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
    }
}
