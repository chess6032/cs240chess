package dataaccess.exceptions;

import dataaccess.DataAccessException;

public class LoginFailException extends DataAccessException {
    public LoginFailException(String message) {
        super(message);
    }
}
