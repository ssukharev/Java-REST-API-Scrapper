package com.example.scrapper.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiParser {
    private static final Logger logger = LoggerFactory.getLogger(ApiParser.class);

    public String parse(String apiType, String rawData) {
        switch (apiType.toLowerCase()) {
            case "currentsapi":
                logger.debug("Parsing data from CurrentsAPI");
                break;
            case "newsapi":
                logger.debug("Parsing data from NewsAPI");
                break;
            case "openweathermap":
                logger.debug("Parsing data from OpenWeatherMap");
                break;
            default:
                logger.warn("Unknown API type: {}", apiType);
                break;
        }
        return rawData.replace("\n", " ");
    }
}
