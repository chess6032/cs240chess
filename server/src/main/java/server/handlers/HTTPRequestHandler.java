package server.handlers;

import io.javalin.http.Context;

public interface HTTPRequestHandler {
    void handleRequest(Context ctx);
}
