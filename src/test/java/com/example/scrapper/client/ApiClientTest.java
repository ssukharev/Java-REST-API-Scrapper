package com.example.scrapper.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ApiClientTest {

    private final ApiClient client = new ApiClient();

    @Test
    public void testFetchData() {
        // Тест CurrentsApi
        assertDoesNotThrow(() -> {
            String data = client.fetchData("currentsapi");
            assertNotNull(data, "Данные, полученные от CurrentsApi, не должны быть null");
            assertTrue(data.contains("\"status\""), "Ответ должен содержать поле 'status'");
        }, "Метод fetchData не должен выбрасывать исключение для CurrentsApi");

        // Тест NewsApi
        assertDoesNotThrow(() -> {
            String data = client.fetchData("newsapi");
            assertNotNull(data, "Данные, полученные от NewsApi, не должны быть null");
            assertTrue(data.contains("\"status\""), "Ответ должен содержать поле 'status'");
        }, "Метод fetchData не должен выбрасывать исключение для NewsApi");

        // Тест OpenWeatherMap
        assertDoesNotThrow(() -> {
            String data = client.fetchData("openweathermap");
            assertNotNull(data, "Данные, полученные от OpenWeatherMap, не должны быть null");
            assertTrue(data.contains("\"coord\""), "Ответ должен содержать поле 'coord'");
        }, "Метод fetchData не должен выбрасывать исключение для OpenWeatherMap");
    }

    @Test
    public void testGetApiUrlWithInvalidType() {
        ApiClient client = new ApiClient();
        try {
            Method method = ApiClient.class.getDeclaredMethod("getApiUrl", String.class);
            method.setAccessible(true);
            InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
                method.invoke(client, "invalidType");
            });
            // Проверяем, что причиной InvocationTargetException является IllegalArgumentException
            assertTrue(exception.getCause() instanceof IllegalArgumentException,
                    "Причиной исключения должен быть IllegalArgumentException");
        } catch (NoSuchMethodException e) {
            fail("Метод getApiUrl не найден");
        }
    }

}
