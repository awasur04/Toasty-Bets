package com.github.awasur04.ToastyBets.discord;

import com.github.awasur04.ToastyBets.database.DatabaseService;
import com.github.awasur04.ToastyBets.game.GameManager;
import com.github.awasur04.ToastyBets.models.Team;
import com.github.awasur04.ToastyBets.models.User;
import com.github.awasur04.ToastyBets.update.UpdateGames;
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

    //For testing
    private static UpdateGames updateGames;
    @Autowired
    public void setUpdateGames(UpdateGames updateGames) { CommandHandler.updateGames = updateGames; }

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
     * report(P)
     * admin(P)
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

            case "timezone":
                timezoneCommand(sourceUser, event);
                break;

            case "schedule":
                event.reply("Done").queue();
                responseHandler.sendWeeklySchedule(sourceUser);
                break;

            case "bet":
                betCommand(sourceUser, event);
                break;

            case "test":
                break;




        }
    }


    public void joinCommand(User sourceUser, net.dv8tion.jda.api.entities.User discordSource) {
        if (sourceUser != null) {
            discordSource.openPrivateChannel().flatMap(channel -> channel.sendMessage("You are not able to join once already registered, if you believe this is an error please reach out to an admin.")).queue();
        } else {
            sourceUser = databaseService.addNewUser(discordSource.getId(), discordSource.getName());
            responseHandler.newUserSetup(sourceUser);
        }
    }

    public void timezoneCommand(User sourceUser, SlashCommandEvent event) {
        event.deferReply().queue();
        String updateTimeZone = event.getOption("timezone").getAsString().toUpperCase();
        String selectedTimeZone = timeZones.get(updateTimeZone);
        sourceUser.setTimeZone(selectedTimeZone);
        if (!selectedTimeZone.isBlank()) {
            databaseService.updateUser(sourceUser);
            responseHandler.displayHelp(sourceUser);
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
        }catch (Exception e) {
            LogManager.error("Error receiving bet command", e.getMessage());
        }
    }
}
