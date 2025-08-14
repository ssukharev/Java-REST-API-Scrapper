package com.example.scrapper.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для записи данных в CSV или JSON.
 * При CSV-формате данные для каждого API накапливаются отдельно и при закрытии файла
 * записываются как отдельные таблицы с заголовками и пустой строкой между ними.
 */
public class FileWriterUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileWriterUtil.class);

    private final String fileName;
    private final String fileFormat;
    private final ObjectMapper objectMapper;
    // Для JSON — список, куда сохраняются все записи
    private final List<Scrapped> records;

    // Для CSV: отдельные списки строк для каждого API
    private final List<String> newsApiRows = new ArrayList<>();
    private final List<String> currentsApiRows = new ArrayList<>();
    private final List<String> openWeatherMapRows = new ArrayList<>();

    public FileWriterUtil(String fileName, String fileFormat) {
        this.fileName = fileName;
        this.fileFormat = fileFormat;
        this.objectMapper = new ObjectMapper();
        this.records = new ArrayList<>();

        if ("CSV".equalsIgnoreCase(fileFormat)) {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        } else if ("JSON".equalsIgnoreCase(fileFormat)) {
            File file = new File(fileName);
            if (file.exists() && file.length() > 0) {
                try {
                    List<Scrapped> existingRecords = objectMapper.readValue(file, new TypeReference<>() {});
                    records.addAll(existingRecords);
                } catch (IOException e) {
                    logger.error("Не удалось прочитать существующий JSON-файл. Будет создан новый.", e);
                }
            }
        }
    }

    /**
     * Метод вызывается из задач для записи данных.
     * Для CSV данные накапливаются, для JSON – сразу добавляются в список.
     */
    public synchronized void writeData(String source, String data) {
        if ("CSV".equalsIgnoreCase(fileFormat)) {
            processCsvData(source, data);
        } else if ("JSON".equalsIgnoreCase(fileFormat)) {
            addJsonRecord(source, data);
        }
    }

    private void processCsvData(String source, String data) {
        try {
            switch (source.toLowerCase()) {
                case "newsapi":
                    newsApiRows.addAll(processNewsApiData(data));
                    break;
                case "currentsapi":
                    currentsApiRows.addAll(processCurrentsApiData(data));
                    break;
                case "openweathermap":
                    openWeatherMapRows.addAll(processOpenWeatherMapData(data));
                    break;
                default:
                    logger.warn("Неизвестный источник для CSV: {}", source);
                    break;
            }
        } catch (IOException e) {
            logger.error("Ошибка обработки CSV данных для источника " + source, e);
        }
    }

    private List<String> processNewsApiData(String data) throws IOException {
        List<String> rows = new ArrayList<>();
        JsonNode root = objectMapper.readTree(data);
        String status = root.path("status").asText("");
        String totalResults = root.path("totalResults").asText("");
        JsonNode articles = root.path("articles");
        if (articles.isArray()) {
            for (JsonNode article : articles) {
                String title = article.path("title").asText("");
                String author = article.path("author").asText("");
                String description = article.path("description").asText("");
                rows.add(String.format("NewsApi,%s,%s,%s,%s,%s",
                        status,
                        totalResults,
                        escapeCsv(title),
                        escapeCsv(author),
                        escapeCsv(description)));
            }
        } else {
            rows.add(String.format("NewsApi,%s,%s,,,,", status, totalResults));
        }
        return rows;
    }

    private List<String> processCurrentsApiData(String data) throws IOException {
        List<String> rows = new ArrayList<>();
        JsonNode root = objectMapper.readTree(data);
        String status = root.path("status").asText("");
        JsonNode newsArray = root.path("news");
        if (newsArray.isArray()) {
            for (JsonNode newsItem : newsArray) {
                String id = newsItem.path("id").asText("");
                String title = newsItem.path("title").asText("");
                String description = newsItem.path("description").asText("");
                // Обрезаем описание, чтобы убрать лишние строки (например, "UPDATE" и последующий текст)
                String trimmedDescription = trimCurrentsDescription(description);
                rows.add(String.format("CurrentsApi,%s,%s,%s,%s",
                        status,
                        escapeCsv(id),
                        escapeCsv(title),
                        escapeCsv(trimmedDescription)));
            }
        } else {
            rows.add(String.format("CurrentsApi,%s,,,,", status));
        }
        return rows;
    }

    private List<String> processOpenWeatherMapData(String data) throws IOException {
        List<String> rows = new ArrayList<>();
        JsonNode root = objectMapper.readTree(data);
        double lon = root.path("coord").path("lon").asDouble(Double.NaN);
        double lat = root.path("coord").path("lat").asDouble(Double.NaN);
        JsonNode weatherArray = root.path("weather");
        String weatherId = "";
        String weatherMain = "";
        String weatherDescription = "";
        if (weatherArray.isArray() && weatherArray.size() > 0) {
            JsonNode weather = weatherArray.get(0);
            weatherId = weather.path("id").asText("");
            weatherMain = weather.path("main").asText("");
            weatherDescription = weather.path("description").asText("");
        }
        rows.add(String.format("OpenWeatherMap,%.4f,%.4f,%s,%s,%s",
                lon,
                lat,
                weatherId,
                escapeCsv(weatherMain),
                escapeCsv(weatherDescription)));
        return rows;
    }

    /**
     * Вспомогательный метод для экранирования значений CSV.
     */
    private String escapeCsv(String input) {
        if (input == null) return "";
        String result = input.replace("\"", "\"\"");
        if (result.contains(",") || result.contains("\"") || result.contains("\n")) {
            result = "\"" + result + "\"";
        }
        return result;
    }

    private void addJsonRecord(String source, String rawJson) {
        try {
            JsonNode jsonNode = objectMapper.readTree(rawJson);
            records.add(new Scrapped(source, jsonNode));
        } catch (JsonProcessingException e) {
            logger.error("Не удалось распарсить JSON данные для источника = " + source, e);
        }
    }

    private void writeJsonToFile() {
        try {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(new File(fileName), records);
        } catch (IOException e) {
            logger.error("Ошибка записи JSON в файл", e);
        }
    }

    /**
     * Метод closeFile() для CSV записывает накопленные данные для каждого API в заданном порядке,
     * добавляя заголовки и пустую строку между таблицами.
     */
    public synchronized void closeFile() {
        if ("JSON".equalsIgnoreCase(fileFormat)) {
            writeJsonToFile();
        } else if ("CSV".equalsIgnoreCase(fileFormat)) {
            writeCsvFile();
        }
    }

    private void writeCsvFile() {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            // Записываем таблицу для NewsApi, если данные есть
            if (!newsApiRows.isEmpty()) {
                writer.write("source,status,totalResults,articleTitle,articleAuthor,articleDescription\n");
                for (String row : newsApiRows) {
                    writer.write(row + "\n");
                }
                writer.write("\n");
            }
            // Записываем таблицу для CurrentsApi, если данные есть
            if (!currentsApiRows.isEmpty()) {
                writer.write("source,status,newsId,newsTitle,newsDescription\n");
                for (String row : currentsApiRows) {
                    writer.write(row + "\n");
                }
                writer.write("\n");
            }
            // Записываем таблицу для OpenWeatherMap, если данные есть
            if (!openWeatherMapRows.isEmpty()) {
                writer.write("source,coordLon,coordLat,weatherId,weatherMain,weatherDescription\n");
                for (String row : openWeatherMapRows) {
                    writer.write(row + "\n");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            logger.error("Ошибка записи финального CSV файла", e);
        }
    }

    /**
     * Метод для обрезки описания CurrentsAPI.
     * Если описание содержит "UPDATE" или перевод строки, оставляет только первую часть.
     */
    private String trimCurrentsDescription(String description) {
        if (description == null) return "";
        // Обрезаем по слову "UPDATE", если оно встречается
        int updateIndex = description.indexOf("UPDATE");
        if (updateIndex != -1) {
            description = description.substring(0, updateIndex);
        }
        // Обрезаем по первому переводу строки
        int newlineIndex = description.indexOf("\n");
        if (newlineIndex != -1) {
            description = description.substring(0, newlineIndex);
        }
        return description.trim();
    }
}
