package dataaccess.exceptions;

import dataaccess.DataAccessException;

public class AuthTokenNotFoundException extends DataAccessException {
    public AuthTokenNotFoundException(String message) {
        super(message);
    }
}
