package com.github.awasur04.ToastyBets.discord;

import com.github.awasur04.ToastyBets.database.DatabaseService;
import com.github.awasur04.ToastyBets.exceptions.GameLockedException;
import com.github.awasur04.ToastyBets.game.GameManager;
import com.github.awasur04.ToastyBets.models.Team;
import com.github.awasur04.ToastyBets.models.User;
import com.github.awasur04.ToastyBets.models.enums.PermissionLevel;
import com.github.awasur04.ToastyBets.utilities.LogManager;
import com.github.awasur04.ToastyBets.utilities.TeamList;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CommandHandler extends ListenerAdapter {

    private static DatabaseService databaseService;
    private static ResponseHandler responseHandler;
    private static GameManager gameManager;

    @Autowired
    public void setDatabaseService(DatabaseService ds) {
        CommandHandler.databaseService = ds;
    }

    @Autowired
    public void setResponseHandler(ResponseHandler rs) {
        CommandHandler.responseHandler = rs;
    }

    @Autowired
    public void setGameManager(GameManager gm) {
        CommandHandler.gameManager = gm;
    }

    public static HashMap<String, String> timeZones = new HashMap<>() {{
        put("EST","America/New_York");
        put("CST","America/Chicago");
        put("MST","America/Denver");
        put("PST","America/Los_Angeles");
        put("BST","Europe/London");
    }};

    /**
     * commands:
     * join
     * timezone
     * schedule
     * bet
     * help
     * deactivate
     * dev
     * report(P)
     * admin(P)  reset, changeperms, setdefaultchannel, banUser, unbanuser, setgenlimits
     * give(P)
     * generate(P)
     * redeem(P)
     */


    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        net.dv8tion.jda.api.entities.User source = event.getUser();
        User sourceUser = databaseService.findUser(source.getId());

        switch(event.getName().toLowerCase()) {
            case "join":
                event.reply("Please check your private messages.").queue();
                joinCommand(sourceUser, source);
                break;

            case "help":
                event.reply("Done").queue();
                responseHandler.displayHelp(sourceUser);
                break;

            case "timezone":
                if (permissionCheck(sourceUser, PermissionLevel.NORMAL)) {
                    timezoneCommand(sourceUser, event);
                } else {
                    event.reply("Sorry you do not have access to that command.").queue();
                }
                break;

            case "schedule":
                if (permissionCheck(sourceUser, PermissionLevel.NORMAL)) {
                    event.reply("Done").queue();
                    responseHandler.removeScheduleCache(source.getId());
                    responseHandler.sendWeeklySchedule(sourceUser);
                } else {
                    event.reply("Sorry you do not have access to that command.").queue();
                }
                break;

            case "bet":
                if (permissionCheck(sourceUser, PermissionLevel.NORMAL)) {
                    betCommand(sourceUser, event);
                } else {
                    event.reply("Sorry you do not have access to that command.").queue();
                }
                break;

            case "dev":
                if (permissionCheck(sourceUser, PermissionLevel.DEV)) {
                    System.out.println("test");
                } else {
                    event.reply("Sorry you do not have access to that command.").queue();
                }
                break;

            case "deactivate":
                deactivateCommand(sourceUser, source);
                event.reply("Done").queue();
                break;

            }
    }

    public void joinCommand(User sourceUser, net.dv8tion.jda.api.entities.User discordSource) {
        if (sourceUser == null) {
            sourceUser = databaseService.addNewUser(discordSource.getId(), discordSource.getName());
            responseHandler.newUserSetup(sourceUser);
        } else if (sourceUser.getPermissionLevel() == PermissionLevel.INACTIVE) {
            sourceUser.setPermissionLevel(PermissionLevel.NORMAL);
            float userBalance = sourceUser.getToastyCoins();
            databaseService.updateUser(sourceUser);
            discordSource.openPrivateChannel().queue(channel -> channel.sendMessage("Welcome back, your current Toasty Coin balance is: " + userBalance).queue());
        } else {
            discordSource.openPrivateChannel().queue(channel -> channel.sendMessage("You are already an active member, if you believe there is an error please reach out to an admin.").queue());
        }
    }

    public void deactivateCommand(User sourceUser, net.dv8tion.jda.api.entities.User discordSource) {
        if (sourceUser == null) {
            discordSource.openPrivateChannel().queue(channel -> channel.sendMessage("Use /join to activate Toasty-Bets").queue());
        } else {
            sourceUser.setPermissionLevel(PermissionLevel.INACTIVE);
            databaseService.updateUser(sourceUser);
            discordSource.openPrivateChannel().queue(channel -> channel.sendMessage("Your account has been disabled, You may join back anytime using /join").queue());
        }
    }

    public void timezoneCommand(User sourceUser, SlashCommandEvent event) {
        event.deferReply().queue();
        String updateTimeZone = event.getOption("timezone").getAsString().toUpperCase();
        String selectedTimeZone = timeZones.get(updateTimeZone);
        sourceUser.setTimeZone(selectedTimeZone);
        if (!selectedTimeZone.isBlank()) {
            databaseService.updateUser(sourceUser);
            responseHandler.displayGameInfo(sourceUser);
            responseHandler.sendWeeklySchedule( sourceUser);
            event.getHook().sendMessage("Success").queue();
        } else {
            event.getHook().sendMessage("Invalid Time zone, please try again").queue();
        }
    }

    public void betCommand(User sourceUser, SlashCommandEvent event) {
        event.deferReply().queue();
        try {
            String teamAbbreviation = event.getOption("team_abbreviation").getAsString().toUpperCase();
            int betAmount = Integer.valueOf(event.getOption("amount").getAsString());
            Team betTeam = TeamList.getTeamByAbbreviation(teamAbbreviation);
            if (!teamAbbreviation.isBlank() && betAmount > 0 && betTeam != null) {
                if (sourceUser.getToastyCoins() >= betAmount) {
                    gameManager.createNewBet(sourceUser, betTeam, betAmount);
                    event.getHook().sendMessage("Success " + teamAbbreviation + " Amount: " + betAmount).queue();
                } else {
                    event.getHook().sendMessage("Error you only have " + sourceUser.getToastyCoins() + " Toasty Coins available.").queue();
                }
            } else {
                event.getHook().sendMessage("Error please input a valid team abbreviation and bet amount").queue();
            }
        }catch (GameLockedException gm) {
            event.getHook().sendMessage("Sorry, that game is already locked").queue();
        }catch (Exception e) {
            LogManager.error("Error receiving bet command", e.getMessage());
        }
    }

    public boolean permissionCheck(User source, PermissionLevel requiredLevel) {
        if (source.getPermissionLevel().isGreaterThan(requiredLevel)) {
            return true;
        } else {
            return false;
        }
    }
}
