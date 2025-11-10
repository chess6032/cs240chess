package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.sqldao.SqlUserDAO;
import exception.ResponseException;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    private final UserDAO userDAO;

    public ServerFacade(String url) {
        serverUrl = url;
        try {
            userDAO = new SqlUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

//    public Pet addPet(Pet pet) throws ResponseException {
//        var request = buildRequest("POST", "/pet", pet);
//        var response = sendRequest(request);
//        return handleResponse(response, Pet.class);
//    }
//
//    public void deletePet(int id) throws ResponseException {
//        var path = String.format("/pet/%s", id);
//        var request = buildRequest("DELETE", path, null);
//        var response = sendRequest(request);
//        handleResponse(response, null);
//    }
//
//    public void deleteAllPets() throws ResponseException {
//        var request = buildRequest("DELETE", "/pet", null);
//        sendRequest(request);
//    }
//
//    public PetList listPets() throws ResponseException {
//        var request = buildRequest("GET", "/pet", null);
//        var response = sendRequest(request);
//        return handleResponse(response, PetList.class);
//    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                // TODO: path should not be REQUIRED to have '/' at the beginning
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    // ^ ------------- PET SHOP ---------------
    // v ------------- me ---------------------

    public void clear() {
        var request = buildRequest("DELETE", "db", null);
        HttpResponse<String> response = sendRequest(request);

        handleResponse(response, null);
    }



}