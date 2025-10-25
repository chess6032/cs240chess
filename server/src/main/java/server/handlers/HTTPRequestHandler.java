package server.handlers;

import io.javalin.http.Context;

public abstract interface HTTPRequestHandler {
    void handleRequest(Context ctx);
}
