package dataaccess.exceptions;

import dataaccess.DataAccessException;

public class GameNotFoundException extends DataAccessException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
