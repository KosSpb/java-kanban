package network;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String urlOfKVServer;
    private final String apiToken;
    private final HttpClient httpClient;

    public KVTaskClient(String urlOfKVServer) {
        this.urlOfKVServer = urlOfKVServer;
        this.httpClient = HttpClient.newHttpClient();
        this.apiToken = register(urlOfKVServer);
    }

    public void put(String key, String json) {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlOfKVServer + "save/" + key + "?API_TOKEN=" + apiToken))
                .POST(body)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("При ответе на POST запрос сервер вернул ошибку с кодом: "
                        + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Во время выполнения POST запроса к KVServer возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlOfKVServer + "load/" + key + "?API_TOKEN=" + apiToken))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("При ответе на GET запрос сервер вернул ошибку с кодом: "
                        + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Во время выполнения GET запроса к KVServer возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    private String register(String urlOfKVServer) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlOfKVServer + "register"))
                .GET()
                .build();
        try {
             return httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Во время выполнения запроса к KVServer по регистрации API_TOKEN " +
                    "возникла ошибка.\nПроверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}
