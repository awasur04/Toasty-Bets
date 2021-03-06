package com.github.awasur04.ToastyBets.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class LogManager {
    static File logFile;
    static File folder;
    static BufferedWriter buffWriter;

    public static void initialize() {
        LocalDateTime currentDateAndTime = LocalDateTime.now(ZoneId.of("America/Chicago"));
        String logFileName = "logs-" + currentDateAndTime.getYear() + "-" + currentDateAndTime.getMonthValue() + "-" + currentDateAndTime.getDayOfMonth() + ".txt";

        folder = new File("logs\\");
        if (!folder.exists()) {
            folder.mkdir();
        }
        logFile = new File(folder, logFileName);
    }

    public static void logMessage(String message) {
        try {
            initialize();
            LocalDateTime currentDateAndTime = LocalDateTime.now(ZoneId.of("America/Chicago"));
            buffWriter = new BufferedWriter(new FileWriter(logFile, true));
            if (!message.isBlank()) {
                String newMessage = "\n[" + currentDateAndTime.getYear() + "-" + currentDateAndTime.getMonthValue() + "-" + currentDateAndTime.getDayOfMonth() +
                        "|" + currentDateAndTime.getHour() + ":" + currentDateAndTime.getMinute() + ":" + currentDateAndTime.getSecond() + "] " + message;
                buffWriter.write(newMessage);
                buffWriter.flush();
            }
        }catch (Exception e) {
            System.out.println("Error: Log file could not be created\n" + e.getMessage());
        }
    }

    public static void closeLogs() {
        try {
            buffWriter.close();
        } catch (IOException e) {
            System.out.println("Error: Log System has not been created!");
        } catch (Exception e) {
            System.out.println("Error: Log system failed to close");
        }
    }

    public static void log(String message) {
        logMessage("LOG: " + message);
    }

    public static void warning(String message) {
        logMessage("WARNING: " + message);
    }

    public static void error(String message, String error) {
       logMessage("ERROR: " + message + " " + error);
    }
}
