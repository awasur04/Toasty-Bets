package com.github.awasur04.toastybets.discord;

import com.github.awasur04.toastybets.database.DatabaseManager;
import com.github.awasur04.toastybets.managers.GameManager;
import com.github.awasur04.toastybets.managers.LogManager;
import com.github.awasur04.toastybets.models.User;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.ZoneId;
import java.util.HashMap;

import static net.dv8tion.jda.api.utils.data.DataType.STRING;

public class DiscordManager {
    private String token;
    private GameManager gameManager;
    private DatabaseManager databaseManager;
    private JDA toastyBot;
    private ResponseHandler responseHandler;

    private static HashMap<String, String> timeZoneUTC = new HashMap<>() {{
        put("EST", "UTC-4");
        put("CST", "UTC-5");
        put("MST", "UTC-6");
        put("PST", "UTC-7");
        put("BST", "UTC+0");
    }};


    public DiscordManager(String token, GameManager gameManager) {
        this.token = token;
        this.gameManager = gameManager;
        this.databaseManager = gameManager.getDB();
        initialize();
    }

    public void initialize() {
        try {
            toastyBot = JDABuilder.createDefault(this.token).setActivity(Activity.playing("Coming soon ;)")).build();
            responseHandler = new ResponseHandler(toastyBot);

            //EVENT LISTENERS
            toastyBot.addEventListener(new CommandHandler(this));


            //COMMANDS
            toastyBot.upsertCommand("join", "Join Toasty Bets Today!").queue();
            toastyBot.upsertCommand(new CommandData("register", "Register your account").
                    addOption(OptionType.STRING, "timezone", "time zone abbreviation", true)).queue();
        }catch (Exception e) {
            LogManager.error("Failed to start discord services :", e.getMessage());
        }

    }

    public boolean createUser(String discordId, String discordName) {
        try {
            User newUser = databaseManager.createUser(discordId, discordName);
            responseHandler.newUserSetup(newUser);
            if (newUser == null) {
                throw new RuntimeException("New User returned null");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public User getUser(String discordId) {
        return databaseManager.getUser(discordId);
    }

    public boolean checkIfUserIsCreated(String discordId) {
        if (databaseManager.getUser(discordId) == null) {
            return false;
        }
        return true;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }
}
