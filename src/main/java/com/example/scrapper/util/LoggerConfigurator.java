package com.example.scrapper.util;

import java.io.File;

/**
 * Класс для программной настройки SLF4J Simple Logger.
 * Вызывайте метод configure() как можно раньше (например, в статическом блоке Main),
 * чтобы системные свойства установились до создания логгеров.
 */
public class LoggerConfigurator {
    public static void configure() {
        // Создаем директорию для логов
        File logDir = new File("src/main/resources/logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.logFile", "src/main/resources/logs/app.log");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }
}
