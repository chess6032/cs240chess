package chess;

/**
 * Indicates an invalid move was made in a game
 */
public class InvalidMoveException extends Exception {

    private final MoveError err;

    public enum MoveError {
        GAME_ALREADY_OVER,
        NO_PIECE_AT_START_POS,
        NOT_PLAYERS_PIECE,
        ILLEGAL_MOVE
    }

    public InvalidMoveException(MoveError err) {
        super(err.name());
        this.err = err;
    }

    public MoveError getMoveError() {
        return err;
    }
}
