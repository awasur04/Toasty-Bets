package com.github.awasur04.toastybets.discord;

import com.github.awasur04.toastybets.database.DatabaseManager;
import com.github.awasur04.toastybets.exceptions.LowBalanceException;
import com.github.awasur04.toastybets.exceptions.TeamNotFoundException;
import com.github.awasur04.toastybets.game.BetManager;
import com.github.awasur04.toastybets.game.GameManager;
import com.github.awasur04.toastybets.models.Bet;
import com.github.awasur04.toastybets.models.Team;
import com.github.awasur04.toastybets.utilities.LogManager;
import com.github.awasur04.toastybets.models.User;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashMap;

public class DiscordManager {
    private String token;
    private GameManager gameManager;
    private DatabaseManager databaseManager;
    private JDA toastyBot;
    private ResponseHandler responseHandler;
    private BetManager betManager;

    private static HashMap<String, String> timeZoneUTC = new HashMap<>() {{
        put("EST", "UTC-4");
        put("CST", "UTC-5");
        put("MST", "UTC-6");
        put("PST", "UTC-7");
        put("BST", "UTC+0");
    }};


    public DiscordManager(String token, GameManager gameManager, BetManager betManager) {
        this.token = token;
        this.gameManager = gameManager;
        this.databaseManager = gameManager.getDB();
        this.betManager = betManager;
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
            toastyBot.upsertCommand("test", "used for testing purposes").queue();
            toastyBot.upsertCommand(new CommandData("bet", "Place a bet")
                    .addOption(OptionType.STRING, "team_abbreviation", "Abbreviation of the team you would like to bet on", true)
                    .addOption(OptionType.INTEGER, "amount", "Amount you would to bet", true)).queue();
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

    public void displayHelp(String discordId) {
        responseHandler.displayHelp(discordId);
    }

    public void printWeeklySchedule(String discordId) {
        try {
            User source = databaseManager.getUser(discordId);
            responseHandler.sendWeeklySchedule(source, gameManager.getGameList(), gameManager.getCurrentWeek());
        }catch (Exception e) {
            LogManager.error("Error printing test schedule: " , e.getMessage());
        }

    }


    public boolean updateTimeZone(String discordId, String discordName, String userZone) {
        try {
            User currentUser = databaseManager.getUser(discordId);
            if (currentUser == null) {
                createUser(discordId, discordName);
            }


            if (timeZoneUTC.get(userZone) == null) return false;
            currentUser.setTimeZone(timeZoneUTC.get(userZone));
            databaseManager.updateUser(currentUser);
            return true;
        }catch (Exception e) {
            LogManager.error("DiscordManager: Failed updateTimeZone: ", e.getMessage());
        }
        return false;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public void createNewBet(String userId, String teamAbbreviation, int betAmount) {
        try {

            //CHECK TIME ZONE SETTINGS DAYLIGHT SAVING

            User targetUser = databaseManager.getUser(userId);
            Team targetTeam = gameManager.getTeamByAbbreviation(teamAbbreviation);
            if (targetUser != null) {
                if (targetUser.getToastyCoins() >= betAmount) {

                } else {
                    throw new LowBalanceException("You only have " + targetUser.getToastyCoins() + " available Toasty Coins");
                }
            } else {
                throw new NullPointerException("User cannot be found");
            }
        }catch(LowBalanceException lb) {

        }catch(TeamNotFoundException te) {

        }catch (Exception e) {
            LogManager.error("Error creating new bet", e.getStackTrace().toString());
        }
    }
}
