package com.github.awasur04.toastybets.database;

import com.github.awasur04.toastybets.managers.LogManager;
import com.github.awasur04.toastybets.models.User;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private String dbUrl;
    private String dbDriver;
    private String dbUser;
    private String dbPassword;

    private int startingTokenAmount = 1000;


    public DatabaseManager() {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            this.dbUrl = properties.getProperty("jdbc.url");
            this.dbDriver = properties.getProperty("jdbc.driver");
            this.dbUser = properties.getProperty("jdbc.username");
            this.dbPassword = properties.getProperty("jdbc.password");
        }catch (IOException e) {
            LogManager.error("Failed to locate properties file", e.getMessage());
        }catch (Exception e) {
            LogManager.error("Database manager failed to initialize", e.getMessage());
        }
    }

}
