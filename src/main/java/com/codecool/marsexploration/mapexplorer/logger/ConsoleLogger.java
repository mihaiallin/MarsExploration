package com.codecool.marsexploration.mapexplorer.logger;

import java.time.LocalDateTime;

public class ConsoleLogger implements Logger {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    @Override
    public void logInfo(String message) {
        String entry = "[" + LocalDateTime.now() + "] INFO : " + message;
        System.out.println(ANSI_GREEN + entry + ANSI_RESET);
    }

    @Override
    public void logError(String message) {
        String entry = "[" + LocalDateTime.now() + "] ERROR : " + message;
        System.out.println(ANSI_RED + entry + ANSI_RESET);
    }
}
