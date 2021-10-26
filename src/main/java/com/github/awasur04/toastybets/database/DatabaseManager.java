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

    public User getUser(String discordId) {
        LogManager.log("DB: Retrieving user results for: " + discordId);
        if (!discordId.isBlank()) {
            String sqlStatement = "SELECT * FROM users WHERE DiscordId =" + "'" + discordId + "'";
            Connection conn = null;
            Statement stmt = null;
            try {
                User tempUser = null;
                Class.forName(dbDriver);
                conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                stmt = conn.createStatement();
                ResultSet results = stmt.executeQuery(sqlStatement);
                while(results.next()) {
                    tempUser = new User(Long.parseLong(results.getString("DiscordId")), results.getString("DiscordName"), results.getInt("Balance"));
                }
                conn.close();
                stmt.close();
                return tempUser;
            } catch (SQLException se) {
                LogManager.error("SQL ERROR: ", se.getMessage());
            } catch (Exception e) {
                LogManager.error("Error retrieving user data\n", e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (Exception e) {
                    LogManager.error("DB: failed to close dbConnection", e.getMessage());
                } finally {
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    } catch (Exception e) {
                        LogManager.error("DB: failed to close dbStatement", e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    public boolean createUser(String discordId, String discordName) {

        LogManager.log("DB: Creating user: " + discordId);
        if (!discordId.isBlank() && !discordName.isBlank()) {
            String sqlStatement = "INSERT INTO users (DiscordId, DiscordName, Balance) VALUE ('" + discordId + "','" + discordName + "','" + startingTokenAmount + "')";
            Connection conn = null;
            Statement stmt = null;
            try {
                Class.forName(dbDriver);
                conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                stmt = conn.createStatement();
                stmt.execute(sqlStatement);
                conn.close();
                stmt.close();
                return true;
            } catch (SQLException se) {
                LogManager.error("SQL ERROR: ", se.getMessage());
            } catch (Exception e) {
                LogManager.error("Error retrieving user data\n", e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (Exception e) {
                    LogManager.error("DB: failed to close dbConnection", e.getMessage());
                } finally {
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    } catch (Exception e) {
                        LogManager.error("DB: failed to close dbStatement", e.getMessage());
                    }
                }
            }
        }
        return false;
    }

}
