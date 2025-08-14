package com.example.scrapper.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScrappedTest {

    @Test
    void testGettersAndSetters() throws Exception {
        // Создаем JsonNode для тестирования
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree("{\"test\":\"data\"}");

        // Тестируем конструктор по умолчанию
        Scrapped emptyScrapped = new Scrapped();
        assertNull(emptyScrapped.getSource());
        assertNull(emptyScrapped.getData());

        // Тестируем конструктор с параметрами
        Scrapped scrapped = new Scrapped("newsapi", jsonNode);
        assertEquals("newsapi", scrapped.getSource());
        assertEquals(jsonNode, scrapped.getData());

        // Тестируем сеттеры
        scrapped.setSource("currentsapi");
        scrapped.setData(mapper.readTree("{\"other\":\"value\"}"));

        assertEquals("currentsapi", scrapped.getSource());
        assertEquals("value", scrapped.getData().get("other").asText());
    }
}
