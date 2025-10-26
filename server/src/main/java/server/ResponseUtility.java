package server;

import com.google.gson.JsonObject;
import io.javalin.http.Context;
import com.google.gson.Gson;

public interface ResponseUtility {
    int SUCCESS_STATUS = 200;
    int BAD_REQUEST_STATUS = 400;
    String BAD_REQUEST_MSG = "Error: bad request";
    int UNAUTHORIZED_STATUS = 401;
    String UNAUTHORIZED_MSG = "Error: unauthorized";
    int ALREADY_TAKEN_STATUS = 403;
    String ALREADY_TAKEN_MSG = "Error: already taken";
    int GENERAL_STATUS = 500;
    int NO_MATCH = -1;

    // 400
    static void badRequestResponse(Context ctx) {
        ctx.status(BAD_REQUEST_STATUS);
        ctx.json(new Gson().toJson(new ErrorMessage(BAD_REQUEST_MSG)));
    }

    // 403
    static void alreadyTakenResponse(Context ctx) {
        ctx.status(ALREADY_TAKEN_STATUS);
        ctx.json(new Gson().toJson(new ErrorMessage(ALREADY_TAKEN_MSG)));
    }

    // 401
    static void unauthorizedResponse(Context ctx) {
        ctx.status(UNAUTHORIZED_STATUS);
        ctx.json(new Gson().toJson(new ErrorMessage(UNAUTHORIZED_MSG)));
    }

    // 200 {}
    static void emptySuccessResponse(Context ctx) {
        ctx.status(SUCCESS_STATUS);
        ctx.json(new Gson().toJson(new JsonObject()));
    }

    // 200
    static void successResponse(Context ctx, String json) {
        ctx.status(SUCCESS_STATUS);
        ctx.json(json);
    }

    static void buildErrorResponse(Context ctx, int status, String message) {
        ctx.status(status);
        ctx.json(new Gson().toJson(new ErrorMessage(message)));
    }
}
