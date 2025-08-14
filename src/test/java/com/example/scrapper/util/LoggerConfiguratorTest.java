package com.example.scrapper.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.File;

public class LoggerConfiguratorTest {

    @Test
    public void testConfigure() {
        LoggerConfigurator.configure();

        assertEquals("info", System.getProperty("org.slf4j.simpleLogger.defaultLogLevel"));
        assertEquals("src/main/resources/logs/app.log", System.getProperty("org.slf4j.simpleLogger.logFile"));
        assertEquals("true", System.getProperty("org.slf4j.simpleLogger.showDateTime"));
        assertEquals("yyyy-MM-dd HH:mm:ss", System.getProperty("org.slf4j.simpleLogger.dateTimeFormat"));
        assertEquals("false", System.getProperty("org.slf4j.simpleLogger.showThreadName"));

        File logDir = new File("src/main/resources/logs");
        assertTrue(logDir.exists() && logDir.isDirectory());
    }
}
