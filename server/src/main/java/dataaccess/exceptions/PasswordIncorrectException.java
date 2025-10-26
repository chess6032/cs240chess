package dataaccess.exceptions;

import dataaccess.DataAccessException;

public class PasswordIncorrectException extends DataAccessException {
    public PasswordIncorrectException(String message) {
        super(message);
    }
}
