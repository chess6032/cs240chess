package dataaccess.exceptions;

import dataaccess.DataAccessException;

public class MissingAttributeException extends DataAccessException {
    public MissingAttributeException(String message) {
        super(message);
    }
}
