package server;

import dataaccess.BadRequestException;
import dataaccess.UsernameAlreadyTakenException;

public interface ExceptionStatusCodes {
    int ALREADY_TAKEN = 403;
    int BAD_REQUEST = 400;
    int GENERIC = 500;
    int NONE = 0;

    static int getCorrespondingStatusCode(Exception e) {
        return NONE;
    }
}
