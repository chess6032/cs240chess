package client;

import com.google.gson.Gson;

import java.util.HashMap;

public class ResponseException extends Exception {

    private final int status;

    public ResponseException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static ResponseException fromJson(int status, String json) {
        var map = new Gson().fromJson(json, HashMap.class); // convert ResponseException to map to make it easier to find stuff
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }
}
