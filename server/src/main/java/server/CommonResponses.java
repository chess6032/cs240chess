package server;

import com.google.gson.JsonObject;
import io.javalin.http.Context;
import com.google.gson.Gson;

public interface CommonResponses {
    int SUCCESS_STATUS = 200;
    int BAD_REQUEST_STATUS = 400;
    String BAD_REQUEST_MSG = "Error: bad request";
    int UNAUTHORIZED_STATUS = 401;
    String UNAUTHORIZED_MSG = "Error: unauthorized";
    int ALREADY_TAKEN_STATUS = 403;
    String ALREADY_TAKEN_MSG = "Error: already taken";
    int GENERIC_STATUS = 500;
    int NO_MATCH = -1;

    // 400
    static void BadRequestResponse(Context ctx) {
        ctx.status(BAD_REQUEST_STATUS);
        ctx.json(new Gson().toJson(new ErrorMessage(BAD_REQUEST_MSG)));
    }

    // 403
    static void AlreadyTakenResponse(Context ctx) {
        ctx.status(ALREADY_TAKEN_STATUS);
        ctx.json(new Gson().toJson(new ErrorMessage(ALREADY_TAKEN_MSG)));
    }

    // 401
    static void UnauthorizedResponse(Context ctx) {
        ctx.status(UNAUTHORIZED_STATUS);
        ctx.json(new Gson().toJson(new ErrorMessage(UNAUTHORIZED_MSG)));
    }

    // 200 {}
    static void EmptySuccessResponse(Context ctx) {
        SuccessResponse(ctx, new JsonObject());
    }

    // 200
    static void SuccessResponse(Context ctx, Object body) {
        ctx.status(SUCCESS_STATUS);
        ctx.json(new Gson().toJson(body));
    }
}
