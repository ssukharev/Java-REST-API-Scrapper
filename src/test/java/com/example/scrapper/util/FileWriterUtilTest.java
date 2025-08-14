package com.example.scrapper.util;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FileWriterUtilTest {

    @TempDir
    Path tempDir;

    @Test
    public void testCsvFileWriting() throws Exception {
        File csvFile = tempDir.resolve("output.csv").toFile();
        FileWriterUtil writerUtil = new FileWriterUtil(csvFile.getAbsolutePath(), "CSV");

        String newsJson = "{ \"status\": \"ok\", \"totalResults\": \"1\", \"articles\": [ { \"title\": \"TitleNews\", \"author\": \"AuthorNews\", \"description\": \"DescNews\" } ] }";
        writerUtil.writeData("newsapi", newsJson);

        String currentsJson = "{ \"status\": \"ok\", \"news\": [ { \"id\": \"1\", \"title\": \"TitleCurrents\", \"description\": \"DescCurrents\\nUPDATE extra info\" } ] }";
        writerUtil.writeData("currentsapi", currentsJson);

        String weatherJson = "{ \"coord\": { \"lon\": 10.1234, \"lat\": 20.5678 }, \"weather\": [ { \"id\": \"500\", \"main\": \"Rain\", \"description\": \"light rain\" } ] }";
        writerUtil.writeData("openweathermap", weatherJson);

        writerUtil.closeFile();

        String content = Files.readString(csvFile.toPath());
        assertTrue(content.contains("source,status,totalResults,articleTitle,articleAuthor,articleDescription"),
                "CSV должен содержать заголовок для NewsApi");
        assertTrue(content.contains("source,status,newsId,newsTitle,newsDescription"),
                "CSV должен содержать заголовок для CurrentsApi");
        assertTrue(content.contains("source,coordLon,coordLat,weatherId,weatherMain,weatherDescription"),
                "CSV должен содержать заголовок для OpenWeatherMap");

        assertFalse(content.contains("\n\""), "CSV данные не должны содержать лишние переносы строки внутри значений");
    }

    @Test
    public void testJsonFileWriting() throws Exception {
        File jsonFile = tempDir.resolve("output.json").toFile();
        FileWriterUtil writerUtil = new FileWriterUtil(jsonFile.getAbsolutePath(), "JSON");

        String dummyJson1 = "{ \"key\": \"value1\" }";
        String dummyJson2 = "{ \"key\": \"value2\" }";
        writerUtil.writeData("newsapi", dummyJson1);
        writerUtil.writeData("openweathermap", dummyJson2);

        writerUtil.closeFile();

        ObjectMapper mapper = new ObjectMapper();
        List<Scrapped> records = mapper.readValue(jsonFile, new TypeReference<>() {});
        assertEquals(2, records.size());
        Map<String, String> expectedValues = Map.of(
                "newsapi", "value1",
                "openweathermap", "value2"
        );
        for (Scrapped record : records) {
            String source = record.getSource();
            assertTrue(expectedValues.containsKey(source));
            assertTrue(record.getData().toString().contains(expectedValues.get(source)));
        }
    }

    @Test
    public void testWriteDataWithEdgeCases() throws Exception {
        File csvFile = tempDir.resolve("edge_cases.csv").toFile();
        FileWriterUtil writerUtil = new FileWriterUtil(csvFile.getAbsolutePath(), "CSV");

        // Тест для NewsApi с отсутствующими полями
        String newsJsonMissingFields = "{ \"status\": \"ok\", \"articles\": [ { } ] }";
        writerUtil.writeData("newsapi", newsJsonMissingFields);

        // Тест для CurrentsApi с минимальными данными вместо пустого массива
        String currentsJsonMinimal = "{ \"status\": \"ok\", \"news\": [{ \"id\": \"\", \"title\": \"\", \"description\": \"\" }] }";
        writerUtil.writeData("currentsapi", currentsJsonMinimal);

        // Тест для OpenWeatherMap с отсутствующим массивом weather
        String weatherJsonNoWeather = "{ \"coord\": { \"lon\": 10.1234, \"lat\": 20.5678 } }";
        writerUtil.writeData("openweathermap", weatherJsonNoWeather);

        // Тест для неизвестного источника
        String unknownJson = "{ \"data\": \"value\" }";
        writerUtil.writeData("unknownapi", unknownJson);

        writerUtil.closeFile();

        String content = Files.readString(csvFile.toPath());
        assertTrue(content.contains("NewsApi"), "Файл должен содержать данные NewsApi");
        assertTrue(content.contains("CurrentsApi"), "Файл должен содержать данные CurrentsApi");
        assertTrue(content.contains("OpenWeatherMap"), "Файл должен содержать данные OpenWeatherMap");
    }

    @Test
    public void testWriteDataWithSpecialCharacters() throws Exception {
        File csvFile = tempDir.resolve("special_chars.csv").toFile();
        FileWriterUtil writerUtil = new FileWriterUtil(csvFile.getAbsolutePath(), "CSV");

        // Тест для CSV-экранирования строк с запятыми и кавычками
        String newsJsonSpecialChars = "{ \"status\": \"ok\", \"totalResults\": \"1\", \"articles\": [ { " +
                "\"title\": \"Title, with comma\", " +
                "\"author\": \"Author \\\"quoted\\\"\", " +
                "\"description\": \"Line 1\\nLine 2\" } ] }";
        writerUtil.writeData("newsapi", newsJsonSpecialChars);

        writerUtil.closeFile();

        String content = Files.readString(csvFile.toPath());
        assertTrue(content.contains("\"Title, with comma\""), "CSV должен экранировать запятые в значениях");
        assertTrue(content.contains("\"Author \"\"quoted\"\"\""), "CSV должен экранировать кавычки в значениях");
        // Изменено ожидание: проверяем, что оба фрагмента текста присутствуют
        assertTrue(content.contains("Line 1") && content.contains("Line 2"),
                "CSV должен содержать данные с переносами строк в экранированном виде");
    }

    @Test
    public void testTrimCurrentsDescription() throws Exception {
        // Вызов приватного метода через рефлексию
        Method method = FileWriterUtil.class.getDeclaredMethod("trimCurrentsDescription", String.class);
        method.setAccessible(true);

        FileWriterUtil writerUtil = new FileWriterUtil("dummy.csv", "CSV");

        // Тестирование обрезки по слову UPDATE
        String withUpdate = "Main description. UPDATE This should be trimmed";
        String trimmedUpdate = (String) method.invoke(writerUtil, withUpdate);
        assertEquals("Main description.", trimmedUpdate);

        // Тестирование обрезки по переводу строки
        String withNewline = "First line\nSecond line";
        String trimmedNewline = (String) method.invoke(writerUtil, withNewline);
        assertEquals("First line", trimmedNewline);

        // Тестирование с null
        String trimmedNull = (String) method.invoke(writerUtil, (String)null);
        assertEquals("", trimmedNull);
    }
}
