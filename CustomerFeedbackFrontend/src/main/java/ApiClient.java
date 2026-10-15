import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {

    static final String BASE = "http://localhost:8080/api";

    static final ObjectMapper M = new ObjectMapper();

    static final HttpClient HTTP = HttpClient.newHttpClient();

    // Common method for all HTTP requests
    static JsonNode request(String method, String path, Object body)
            throws Exception {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        HttpRequest.BodyPublisher publisher;

        if (body == null) {
            publisher = HttpRequest.BodyPublishers.noBody();
        } else {
            String json = M.writeValueAsString(body);
            publisher = HttpRequest.BodyPublishers.ofString(json);
        }

        builder.method(method, publisher);

        HttpResponse<String> response = HTTP.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString()
        );

        String responseBody = response.body();

        JsonNode result = responseBody == null || responseBody.isBlank()
                ? M.createObjectNode()
                : M.readTree(responseBody);

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            String error = result.path("message")
                    .asText("Request failed: HTTP "
                            + response.statusCode());

            throw new RuntimeException(error);
        }

        return result;
    }

    // Create JSON data from key-value pairs
    static Map<String, String> data(String... values) {

        if (values.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Provide key-value pairs."
            );
        }

        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }

        return map;
    }

    // GET request
    static JsonNode get(String path) throws Exception {
        return request("GET", path, null);
    }

    // POST request
    static JsonNode post(String path, Object body)
            throws Exception {
        return request("POST", path, body);
    }

    // PUT request
    static JsonNode put(String path, Object body)
            throws Exception {
        return request("PUT", path, body);
    }

    // DELETE request
    static JsonNode delete(String path) throws Exception {
        return request("DELETE", path, null);
    }
}