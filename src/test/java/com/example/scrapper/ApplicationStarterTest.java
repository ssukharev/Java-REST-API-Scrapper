package com.example.scrapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

public class ApplicationStarterTest {

    private ScheduledExecutorService callStartTasks(String[] args) throws Exception {
        Method method = ApplicationStarter.class.getDeclaredMethod("startTasks", String[].class);
        method.setAccessible(true);
        return (ScheduledExecutorService) method.invoke(null, (Object) args);
    }

    @Test
    public void testStartTasksValidServices() throws Exception {
        String[] args = { "2", "5", "newsapi,currentsapi,openweathermap", "CSV" };
        ScheduledExecutorService scheduler = callStartTasks(args);
        assertNotNull(scheduler, "Scheduler не должен быть null при корректных сервисах");
        scheduler.shutdownNow();
    }

    @Test
    public void testStartTasksNoValidServicesThrowsException() {
        System.setProperty("test.mode", "true");
        String[] args = { "2", "5", "invalid1,invalid2", "CSV" };

        Exception thrown = assertThrows(Exception.class, () -> {
            try {
                callStartTasks(args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        });
        assertTrue(thrown instanceof IllegalStateException);
        assertTrue(thrown.getMessage().contains("No valid services"));
        System.clearProperty("test.mode");
    }
}
