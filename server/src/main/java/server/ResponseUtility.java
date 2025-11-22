package server;

import com.google.gson.JsonObject;
import dataaccess.exceptions.SqlException;
import dataaccess.exceptions.SqlInterruptedException;
import io.javalin.http.Context;
import com.google.gson.Gson;

import static server.HttpResponseCodes.*;

public interface ResponseUtility {
    String BAD_REQUEST_MSG = "Error: bad request";
    String UNAUTHORIZED_MSG = "Error: unauthorized";
    String ALREADY_TAKEN_MSG = "Error: already taken";

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

    // 500
    static void failedSerializationResponse(Context ctx, String msg) {
        ctx.status(INTERNAL_ERROR_STATUS);
        ctx.json(new Gson().toJson(msg));
    }

    static void buildErrorResponse(Context ctx, int status, String message) {
        ctx.status(status);
        ctx.json(new Gson().toJson(new ErrorMessage(message)));
    }

    // PHASE 4: SQL errors

    static void handleSqlException(Context ctx, String msg, SqlException e) {
        if (e.getClass().equals(SqlInterruptedException.class)) {
            sqlInterruptedExceptionResponse(ctx, msg, e);
        } else {
            sqlExceptionResponse(ctx, msg, e);
        }
    }

    static void sqlExceptionResponse(Context ctx, String msg, SqlException e) {
        if (ctx == null) {
            return;
        }
        ctx.status(INTERNAL_ERROR_STATUS);
        ctx.json(new Gson().toJson(new ErrorMessage("Error: SQL Exception: " + msg + e.getMessage())));
    }

    static void sqlInterruptedExceptionResponse(Context ctx, String msg, SqlException e) {
        ctx.status(INTERNAL_ERROR_STATUS);
        ctx.json(new Gson().toJson(new ErrorMessage("Error: SQL Connection interrupted: " + msg + e.getMessage())));
    }
}
