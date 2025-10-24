package server;

import dataaccess.UsernameAlreadyTakenException;

public interface ExceptionStatusCode {
    int USERNAME_ALREADY_TAKEN = 403;
    int GENERIC = 500;

    static int getCorrespondingStatusCode(Exception e) {
        if (e.getClass() == UsernameAlreadyTakenException.class) {
            return USERNAME_ALREADY_TAKEN;
        }
        return GENERIC;
    }
}
