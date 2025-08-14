package com.example.scrapper;
import com.example.scrapper.client.ApiClient;
import com.example.scrapper.parser.ApiParser;
import com.example.scrapper.util.ApiScrapperTask;
import com.example.scrapper.util.FileWriterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ApplicationStarter {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStarter.class);
    private static FileWriterUtil fileWriterUtil;

    public static void start(String[] args) {
        if (args.length < 4) {
            logger.info("Usage: java -jar rest-api-scrapper.jar <maxThreads> <timeoutSeconds> <servicesCommaSeparated> <fileFormat(CSV/JSON)>");
            System.exit(1);
        }

        final ScheduledExecutorService scheduler = startTasks(args);

        // Демон-поток для прослушивания ввода команды "exit"
        Thread exitListener = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if ("exit".equalsIgnoreCase(line.trim())) {
                    logger.info("Получена команда exit. Завершаем работу...");
                    scheduler.shutdownNow();
                    break;
                }
            }
            scanner.close();
        });
        exitListener.setDaemon(true);
        exitListener.start();

        try {
            boolean terminated = scheduler.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            if (!terminated) {
                logger.info("Scheduler не завершился вовремя. Принудительное завершение.");
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Main thread interrupted", e);
        }

        // Закрываем файл
        if (fileWriterUtil != null) {
            fileWriterUtil.closeFile();
        }
    }

    private static ScheduledExecutorService startTasks(String[] args) {
        int maxThreads;
        int timeoutSeconds;
        try {
            maxThreads = Integer.parseInt(args[0]);
            timeoutSeconds = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            logger.error("Неверный формат числа для maxThreads или timeoutSeconds", e);
            System.exit(1);
            return null;
        }

        String[] services = args[2].split(",");
        String fileFormat = args[3].toUpperCase();

        // Создаём FileWriterUtil в режиме перезаписи (а не добавления)
        fileWriterUtil = new FileWriterUtil("src/main/resources/output." + fileFormat.toLowerCase(), fileFormat);

        List<String> invalidServices = new ArrayList<>();
        List<String> validServices = new ArrayList<>();
        int validTaskCount = 0;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(maxThreads);

        for (String service : services) {
            service = service.trim();
            ApiScrapperTask task;
            switch (service.toLowerCase()) {
                case "newsapi":
                    task = new ApiScrapperTask(new ApiClient(), new ApiParser(), fileWriterUtil, "newsapi");
                    validServices.add("NewsApi");
                    break;
                case "currentsapi":
                    task = new ApiScrapperTask(new ApiClient(), new ApiParser(), fileWriterUtil, "currentsapi");
                    validServices.add("CurrentsApi");
                    break;
                case "openweathermap":
                    task = new ApiScrapperTask(new ApiClient(), new ApiParser(), fileWriterUtil, "openweathermap");
                    validServices.add("OpenWeatherMap");
                    break;
                default:
                    invalidServices.add(service);
                    continue;
            }
            scheduler.scheduleWithFixedDelay(task, 0, timeoutSeconds, TimeUnit.SECONDS);
            validTaskCount++;
        }

        if (!invalidServices.isEmpty()) {
            logger.info("Следующие сервисы не распознаны: {}", String.join(", ", invalidServices));
        }
        if (!validServices.isEmpty()) {
            logger.info("Обрабатываются следующие сервисы: {}", String.join(", ", validServices));
        }
        if (validTaskCount == 0) {
            logger.info("Нет корректных сервисов для опроса. Завершаем работу.");
            // Если тестовый режим, бросаем исключение, иначе вызываем System.exit(1)
            if (Boolean.getBoolean("test.mode")) {
                throw new IllegalStateException("No valid services");
            } else {
                System.exit(1);
            }
        }


        return scheduler;
    }
}
