package server;

import dataaccess.BadRequestException;
import dataaccess.UsernameAlreadyTakenException;

public interface ExceptionStatusCodes {
    int SUCCESS = 200;
    int ALREADY_TAKEN = 403;
    int BAD_REQUEST = 400;
    int GENERIC = 500;
    int NO_MATCH = -1;
}
