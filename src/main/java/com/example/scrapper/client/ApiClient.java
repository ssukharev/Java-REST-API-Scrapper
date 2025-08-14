package com.example.scrapper.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    // URL шаблоны без API ключей
    private static final String CURRENTS_API_URL_TEMPLATE = "https://api.currentsapi.services/v1/latest-news?apiKey=%s&page_size=1";
    private static final String NEWS_API_URL_TEMPLATE = "https://newsapi.org/v2/everything?q=technology&apiKey=%s&pageSize=1";
    private static final String WEATHER_API_URL_TEMPLATE = "https://api.openweathermap.org/data/2.5/weather?q=London&appid=%s";


    private static final Map<String, String> apiKeys = new HashMap<>();

    // Загрузка ключей API при инициализации класса
    static {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/apiKeys.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    apiKeys.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка при загрузке API ключей", e);
        }
    }

    public String fetchData(String apiType) throws IOException, InterruptedException {
        String apiUrl = getApiUrl(apiType);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        logger.debug("Fetching data from {}", apiType);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String getApiUrl(String apiType) {
        String key;
        return switch (apiType.toLowerCase()) {
            case "currentsapi" -> {
                key = apiKeys.get("currentsapi.key");
                yield String.format(CURRENTS_API_URL_TEMPLATE, key);
            }
            case "newsapi" -> {
                key = apiKeys.get("newsapi.key");
                yield String.format(NEWS_API_URL_TEMPLATE, key);
            }
            case "openweathermap" -> {
                key = apiKeys.get("openweathermap.key");
                yield String.format(WEATHER_API_URL_TEMPLATE, key);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип API: " + apiType);
        };
    }
}
