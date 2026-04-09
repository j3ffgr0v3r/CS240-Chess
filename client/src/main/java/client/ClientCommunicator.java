package client;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import model.exceptions.HTTPException;
import model.requests.Request;
import model.results.Result;

public class ClientCommunicator {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ClientCommunicator(String url) {
        serverUrl = url;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    protected <T extends Result> T post(Request requestModel, String endpoint, String authToken, Class<T> responseType) throws HTTPException {
        var request = buildRequest("POST", endpoint, authToken, requestModel);
        var response = sendRequest(request);
        return handleResponse(response, responseType);
    }

    protected <T extends Result> T get(Request requestModel, String endpoint, String authToken, Class<T> responseType) throws HTTPException {
        var request = buildRequest("GET", endpoint, authToken, requestModel);
        var response = sendRequest(request);
        return handleResponse(response, responseType);
    }

    protected <T extends Result> T put(Request requestModel, String endpoint, String authToken, Class<T> responseType) throws HTTPException {
        var request = buildRequest("PUT", endpoint, authToken, requestModel);
        var response = sendRequest(request);
        return handleResponse(response, responseType);
    }

    protected void delete(Request requestModel, String endpoint, String authToken) throws HTTPException {
        var request = buildRequest("DELETE", endpoint, authToken, requestModel);
        sendRequest(request);
    }


    private HttpRequest buildRequest(String method, String endpoint, String authToken, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + endpoint))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
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

    private HttpResponse<String> sendRequest(HttpRequest request) throws HTTPException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (ConnectException ex) {
            throw new HTTPException(400, "Error: unable to connect with server. Please check your network connection and try again.");
        }catch (IOException | InterruptedException ex) {
            throw new HTTPException(500, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws HTTPException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new HTTPException(status, JsonParser.parseString(body).getAsJsonObject().get("message").getAsString());
            }

            throw new HTTPException(status, "Error: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
