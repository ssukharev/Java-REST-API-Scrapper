package com.example.scrapper;

import com.example.scrapper.util.LoggerConfigurator;

public class Main {
    public static void main(String[] args) {
        LoggerConfigurator.configure();
        ApplicationStarter.start(args);
    }
}
