package com.github.awasur04.toastybets.discord;

import com.github.awasur04.toastybets.database.DatabaseManager;
import com.github.awasur04.toastybets.managers.LogManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class DiscordManager {
    private String token;
    private DatabaseManager databaseManager;
    private JDA toastyBot;


    public DiscordManager(String token, DatabaseManager databaseManager) {
        this.token = token;
        this.databaseManager = databaseManager;
        initialize();
    }

    public void initialize() {
        try {
            toastyBot = JDABuilder.createDefault(this.token).setActivity(Activity.playing("Coming soon ;)")).build();

            //EVENT LISTENERS
            toastyBot.addEventListener(new CommandHandler(this));


            //COMMANDS
            toastyBot.upsertCommand("join", "Register to join toasty bets").queue();
        }catch (Exception e) {
            LogManager.error("Failed to start discord services :", e.getMessage());
        }

    }

    public boolean createUser(String discordId, String discordName) {
        return databaseManager.createUser(discordId, discordName);
    }

    public boolean checkIfUserIsRegistered(String discordId) {
        if (databaseManager.getUser(discordId) == null) {
            return false;
        }
        return true;
    }
}
