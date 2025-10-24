package server;

import io.javalin.http.Context;
import com.google.gson.Gson;

public interface CommonExceptions {
    int SUCCESS_STATUS = 200;
    int ALREADY_TAKEN_STATUS = 403;
    String ALREADY_TAKEN_MSG = "Error: already taken";
    int BAD_REQUEST_STATUS = 400;
    String BAD_REQUEST_MSG = "Error: bad request";
    int GENERIC_STATUS = 500;
    int NO_MATCH = -1;

    static void BadRequestResponse(Context ctx) {
        ctx.status(CommonExceptions.BAD_REQUEST_STATUS); // 400
        ctx.json(new Gson().toJson(new ErrorMessage(BAD_REQUEST_MSG)));
    }

    static void AlreadyTakenResponse(Context ctx) {
        ctx.status(CommonExceptions.ALREADY_TAKEN_STATUS); // 403
        ctx.json(new Gson().toJson(new ErrorMessage(ALREADY_TAKEN_MSG)));
    }
}
