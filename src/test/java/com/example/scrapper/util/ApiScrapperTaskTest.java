package com.example.scrapper.util;

import com.example.scrapper.client.ApiClient;
import com.example.scrapper.parser.ApiParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiScrapperTaskTest {

    @Mock
    private ApiClient mockApiClient;

    @Mock
    private ApiParser mockApiParser;

    @Mock
    private FileWriterUtil mockFileWriterUtil;

    @Test
    void testApiScrapperTask_Normal() throws IOException, InterruptedException {
        // Подготовка тестовых данных
        String rawData = "{\"status\":\"ok\",\"totalResults\":\"5\",\"articles\":[{\"title\":\"Dummy Title\",\"author\":\"Dummy Author\",\"description\":\"Dummy Description\"}]}";
        String parsedData = rawData.replace("\n", " ");

        // Настройка поведения моков
        when(mockApiClient.fetchData("newsapi")).thenReturn(rawData);
        when(mockApiParser.parse("newsapi", rawData)).thenReturn(parsedData);

        // Запуск тестируемого метода
        ApiScrapperTask task = new ApiScrapperTask(mockApiClient, mockApiParser, mockFileWriterUtil, "newsapi");
        task.run();

        // Проверка взаимодействий с моками
        verify(mockApiClient).fetchData("newsapi");
        verify(mockApiParser).parse("newsapi", rawData);
        verify(mockFileWriterUtil).writeData("newsapi", parsedData);
    }

    @Test
    void testApiScrapperTask_UnknownClient() {
        // Создаем объект неправильного типа вместо ApiClient
        Object unknownClient = mock(Object.class);

        // Запуск тестируемого метода
        ApiScrapperTask task = new ApiScrapperTask(unknownClient, mockApiParser, mockFileWriterUtil, "newsapi");
        task.run();

        // Проверка, что методы не были вызваны из-за ошибки приведения типа
        verifyNoInteractions(mockApiParser);
        verifyNoInteractions(mockFileWriterUtil);
    }

    @Test
    void testApiScrapperTask_UnknownParser() throws IOException, InterruptedException {
        // Подготовка тестовых данных
        String rawData = "{\"status\":\"ok\",\"totalResults\":\"5\",\"articles\":[{\"title\":\"Dummy Title\",\"author\":\"Dummy Author\",\"description\":\"Dummy Description\"}]}";

        // Настройка поведения мока ApiClient
        when(mockApiClient.fetchData("newsapi")).thenReturn(rawData);

        // Создаем объект неправильного типа вместо ApiParser
        Object unknownParser = mock(Object.class);

        // Запуск тестируемого метода
        ApiScrapperTask task = new ApiScrapperTask(mockApiClient, unknownParser, mockFileWriterUtil, "newsapi");
        task.run();

        // Проверка, что API клиент был вызван, но FileWriter не был вызван
        verify(mockApiClient).fetchData("newsapi");
        verifyNoInteractions(mockFileWriterUtil);
    }

    @Test
    void testApiScrapperTask_ClientThrowsIOException() throws IOException, InterruptedException {
        // Настройка мока ApiClient на выброс IOException
        when(mockApiClient.fetchData("newsapi")).thenThrow(new IOException("Test IO exception"));

        // Запуск тестируемого метода
        ApiScrapperTask task = new ApiScrapperTask(mockApiClient, mockApiParser, mockFileWriterUtil, "newsapi");
        task.run();

        // Проверка, что остальные методы не были вызваны после исключения
        verify(mockApiClient).fetchData("newsapi");
        verifyNoInteractions(mockApiParser);
        verifyNoInteractions(mockFileWriterUtil);
    }

    @Test
    void testApiScrapperTask_ClientThrowsInterruptedException() throws IOException, InterruptedException {
        // Настройка мока ApiClient на выброс InterruptedException
        when(mockApiClient.fetchData("newsapi")).thenThrow(new InterruptedException("Test interrupted"));

        // Запуск тестируемого метода
        ApiScrapperTask task = new ApiScrapperTask(mockApiClient, mockApiParser, mockFileWriterUtil, "newsapi");
        task.run();

        // Проверка, что остальные методы не были вызваны после исключения
        verify(mockApiClient).fetchData("newsapi");
        verifyNoInteractions(mockApiParser);
        verifyNoInteractions(mockFileWriterUtil);

        // Проверка, что флаг прерывания потока установлен
        boolean interrupted = Thread.interrupted(); // также сбрасывает флаг
        assertTrue(interrupted, "Поток должен быть помечен как прерванный");
    }
}
