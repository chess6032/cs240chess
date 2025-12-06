package server;

import chess.ChessGame;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class ChessGameSerializer {
    public static String newChessGameJson() throws FailedSerializationException {
        return serialize(new ChessGame());
    }

    public static String serialize(ChessGame game) throws FailedSerializationException {
        try {
            return new Gson().toJson(game);
        } catch (JsonSyntaxException e) {
            throw new FailedSerializationException("Failed to serialize ChessGame.");
        }
    }

    public static ChessGame deserialize(String json) throws FailedDeserializationException {
        try {
            return new Gson().fromJson(json, ChessGame.class);
        } catch (JsonSyntaxException e) {
            throw new FailedDeserializationException("Failed to deserialize ChessGame.");
        }
    }

    public static void main(String[] args) {
        try {
            String json = newChessGameJson();
            System.out.println("JSON:\n" + json + '\n');

            ChessGame game = deserialize(json);
            System.out.println("GAME:\n" + game);

            System.out.println("\n\n\n");

            var board = new chess.ChessBoard();

            game.setBoard(board);
            System.out.println("GAME (pre-serialized):" + game + '\n');

            json = serialize(game);
            System.out.println("JSON:\n" + json + '\n');

            System.out.println("GAME (deserialized):\n" + deserialize(json) + '\n');
            System.out.println("uhhh those are the same right? - " + (game.equals(deserialize(json))));

        } catch (FailedSerializationException | FailedDeserializationException e) {
            throw new RuntimeException(e);
        }
    }
}
