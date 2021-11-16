package com.github.awasur04.ToastyBets.discord;

import com.github.awasur04.ToastyBets.database.DatabaseController;
import com.github.awasur04.ToastyBets.models.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class CommandHandler extends ListenerAdapter {

    @Autowired
    private DatabaseController databaseController;
    @Autowired
    private ResponseHandler responseHandler;

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
     * register
     * generate(ND)
     * redeem(ND)
     * bet(ND)
     * schedule(ND)
     * report(ND)
     */


    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        net.dv8tion.jda.api.entities.User source = event.getUser();
        User sourceUser = databaseController.findUser(source.getId());

        if (event.getName().equalsIgnoreCase("join")) {
            event.deferReply().queue();
            if (sourceUser != null) {
                source.openPrivateChannel().flatMap(channel -> channel.sendMessage("You are not able to join once already registered, if you believe this is an error please reach out to an admin.")).queue();
            } else {
                sourceUser = databaseController.addNewUser(source.getId(), source.getName());
                responseHandler.newUserSetup(sourceUser);
            }
            event.getHook().sendMessage("Please check your dm's").queue();

        }else if (event.getName().equalsIgnoreCase("timezone")) {
            event.deferReply().queue();

            String updateTimeZone = event.getOption("timezone").getAsString().toUpperCase();

            sourceUser.setTimeZone(timeZones.get(updateTimeZone));
            if (databaseController.updateUser(sourceUser)) {
                responseHandler.displayHelp(source.getId());
                responseHandler.sendWeeklySchedule( sourceUser);
                event.getHook().sendMessage("Success").queue();
            } else {
                event.getHook().sendMessage("Invalid Time zone, please try again").queue();
            }

       }//else if (event.getName().equalsIgnoreCase("test")) {
//            event.deferReply().queue();
//            discordManager.printWeeklySchedule(event.getUser().getId());
//            event.getHook().sendMessage(";)").queue();
//        } else if (event.getName().equalsIgnoreCase("bet")) {
//            event.deferReply().queue();
//            try {
//                String teamAbbreviation = event.getOption("team_abbreviation").getAsString().toUpperCase();
//                int betAmount = Integer.valueOf(event.getOption("amount").getAsString());
//                if (!teamAbbreviation.isBlank() && betAmount > 0) {
//
//                    event.getHook().sendMessage("Success " + teamAbbreviation + " Amount: " + betAmount).queue();
//                } else {
//                    event.getHook().sendMessage("Error " + teamAbbreviation + " Amount: " + betAmount).queue();
//                }
//            }catch (Exception e) {
//                LogManager.error("Error receiving bet command", e.getStackTrace().toString());
//            }
//        }
    }
}
