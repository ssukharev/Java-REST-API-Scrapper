package com.example.scrapper.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApiParserTest {

    private final ApiParser parser = new ApiParser();

    @Test
    public void testParse() {
        // Тестирование CurrentsApi
        String currentsRaw = "{\"status\":\"ok\",\"news\":[]}\n";
        String currentsParsed = parser.parse("currentsapi", currentsRaw);
        assertFalse(currentsParsed.contains("\n"), "Парсенная строка CurrentsApi не должна содержать символы новой строки");
        assertTrue(currentsParsed.contains("\"status\":\"ok\""), "Парсенная строка CurrentsApi должна содержать поле status");

        // Тестирование NewsApi
        String newsRaw = "{\"status\":\"ok\",\"articles\":[]}\n";
        String newsParsed = parser.parse("newsapi", newsRaw);
        assertFalse(newsParsed.contains("\n"), "Парсенная строка NewsApi не должна содержать символы новой строки");
        assertTrue(newsParsed.startsWith("{\"status\":\"ok\""), "Парсенная строка NewsApi должна начинаться с JSON объекта");

        // Тестирование OpenWeatherMap
        String weatherRaw = "{\"coord\":{\"lon\":10,\"lat\":20},\"weather\":[]}\n";
        String weatherParsed = parser.parse("openweathermap", weatherRaw);
        assertFalse(weatherParsed.contains("\n"), "Парсенная строка OpenWeatherMap не должна содержать символы новой строки");
        assertTrue(weatherParsed.contains("\"coord\":{\"lon\":10"), "Парсенная строка OpenWeatherMap должна содержать координаты");
    }

    @Test
    public void testParseUnknownApiType() {
        ApiParser parser = new ApiParser();
        String unknownRaw = "{\"data\":\"test\"}";
        String result = parser.parse("unknownapi", unknownRaw);

        assertFalse(result.contains("\n"), "Парсенная строка с неизвестным API не должна содержать символы новой строки");
        assertEquals(unknownRaw.replace("\n", " "), result, "Содержимое должно быть заменено без других изменений");
    }

}
