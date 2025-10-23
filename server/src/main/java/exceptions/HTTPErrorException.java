package exceptions;

import chess.model.ErrorMessage;

public class HTTPErrorException extends Exception {

    private final ErrorMessage errorMessage;

    public HTTPErrorException(String message) {
        super(message);
        errorMessage = new ErrorMessage(message);
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
