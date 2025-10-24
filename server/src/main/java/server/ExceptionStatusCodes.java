package server;

import dataaccess.BadRequestException;
import dataaccess.UsernameAlreadyTakenException;

public interface ExceptionStatusCodes {
    int USERNAME_ALREADY_TAKEN = 403;
    int BAD_REQUEST = 400;
    int GENERIC = 500;

    static int getCorrespondingStatusCode(Exception e) {
        if (e.getClass() == UsernameAlreadyTakenException.class) {
            return USERNAME_ALREADY_TAKEN;
        }
        if (e.getClass() == BadRequestException.class) {
            return BAD_REQUEST;
        }
        return GENERIC;
    }
}
