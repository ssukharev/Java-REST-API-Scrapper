package com.example.scrapper.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class ApiScrapperTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ApiScrapperTask.class);
    private final Object apiClient;
    private final Object apiParser;
    private final FileWriterUtil fileWriter;
    private final String apiType;

    public ApiScrapperTask(Object apiClient, Object apiParser, FileWriterUtil fileWriter, String apiType) {
        this.apiClient = apiClient;
        this.apiParser = apiParser;
        this.fileWriter = fileWriter;
        this.apiType = apiType; // Сохраняем тип API при создании задачи
    }

    @Override
    public void run() {
        try {
            try {
                // Используем явно указанный тип API
                String rawData = ((com.example.scrapper.client.ApiClient)apiClient).fetchData(apiType);

                // Обрабатываем полученные данные через объединенный парсер
                String parsedData = ((com.example.scrapper.parser.ApiParser)apiParser).parse(apiType, rawData);

                // Записываем результат
                fileWriter.writeData(apiType, parsedData);
            } catch (ClassCastException e) {
                // Логируем ошибку приведения типов
                logger.error("Ошибка приведения типа для клиента или парсера API", e);
            }
        } catch (InterruptedException e) {
            logger.warn("ApiScrapperTask was interrupted, shutting down gracefully.", e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.error("I/O error occurred in ApiScrapperTask.", e);
        }
    }
}
