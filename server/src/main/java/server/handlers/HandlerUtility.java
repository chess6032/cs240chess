package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.javalin.http.Context;
import server.FailedDeserializationException;
import server.FailedSerializationException;

public interface HandlerUtility {

    static <T> T deserializeBody(Context ctx, Class<T> clazz) throws FailedDeserializationException {
        try {
            return new Gson().fromJson(ctx.body(), clazz);
        } catch (JsonSyntaxException e) {
            throw new FailedDeserializationException("Failed to deserialize. Class: " + clazz.toString() +
                                                        "\nHTTP body: " + ctx.body() );
        }
    }

    static String serialize(Object obj) throws FailedSerializationException {
        String json;
        try {
            json = new Gson().toJson(obj);
        } catch (JsonSyntaxException e) {
            throw new FailedSerializationException("Failed to serialize: " + obj.getClass());
        }
        return json;
    }
}
