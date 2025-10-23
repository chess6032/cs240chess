package dataaccess;

import exceptions.HTTPErrorException;

public class UsernameAlreadyTakenException extends HTTPErrorException {
    public static final int httpStatus = 403;

    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}
