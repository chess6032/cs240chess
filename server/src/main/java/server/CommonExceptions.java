package server;

import io.javalin.http.Context;
import com.google.gson.Gson;

public interface CommonExceptions {
    int SUCCESS_STATUS = 200;
    int BAD_REQUEST_STATUS = 400;
    String BAD_REQUEST_MSG = "Error: bad request";
    int UNAUTHORIZED_STATUS = 401;
    String UNAUTHORIZED_MSG = "Error: unauthorized";
    int ALREADY_TAKEN_STATUS = 403;
    String ALREADY_TAKEN_MSG = "Error: already taken";
    int GENERIC_STATUS = 500;
    int NO_MATCH = -1;

    static void BadRequestResponse(Context ctx) {
        ctx.status(BAD_REQUEST_STATUS); // 400
        ctx.json(new Gson().toJson(new ErrorMessage(BAD_REQUEST_MSG)));
    }

    static void AlreadyTakenResponse(Context ctx) {
        ctx.status(ALREADY_TAKEN_STATUS); // 403
        ctx.json(new Gson().toJson(new ErrorMessage(ALREADY_TAKEN_MSG)));
    }

    static void UnauthorizedResponse(Context ctx) {
        ctx.status(UNAUTHORIZED_STATUS);
        ctx.json(new Gson().toJson(new ErrorMessage(UNAUTHORIZED_MSG)));
    }
}
