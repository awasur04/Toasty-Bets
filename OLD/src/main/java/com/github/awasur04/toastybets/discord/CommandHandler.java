package com.github.awasur04.toastybets.discord;

import com.github.awasur04.toastybets.models.User;
import com.github.awasur04.toastybets.utilities.LogManager;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {
    private DiscordManager discordManager;

    public CommandHandler(DiscordManager dm) {
        this.discordManager = dm;
    }


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
        if (event.getName().equalsIgnoreCase("join")) {
            event.deferReply().queue();
            if (discordManager.checkIfUserIsCreated(source.getId())) {
                source.openPrivateChannel().flatMap(channel -> channel.sendMessage("You are not able to join once already registered, if you believe this is an error please reach out to an admin.")).queue();
            } else {
                discordManager.createUser(source.getId(), source.getName());
            }
            event.getHook().sendMessage("Please check your dm's").queue();
        }else if (event.getName().equalsIgnoreCase("register")) {
            event.deferReply().queue();

            String updateTimeZone = event.getOption("timezone").getAsString().toUpperCase();

            if (discordManager.updateTimeZone(source.getId(), source.getName(), updateTimeZone)) {
                discordManager.displayHelp(source.getId());
                discordManager.printWeeklySchedule(source.getId());
                event.getHook().sendMessage("Success").queue();
            } else {
                event.getHook().sendMessage("Invalid Time zone, please try again").queue();
            }
        }else if (event.getName().equalsIgnoreCase("test")) {
            event.deferReply().queue();
            discordManager.printWeeklySchedule(event.getUser().getId());
            event.getHook().sendMessage(";)").queue();
        } else if (event.getName().equalsIgnoreCase("bet")) {
            event.deferReply().queue();
            try {
                String teamAbbreviation = event.getOption("team_abbreviation").getAsString().toUpperCase();
                int betAmount = Integer.valueOf(event.getOption("amount").getAsString());
                if (!teamAbbreviation.isBlank() && betAmount > 0) {

                    event.getHook().sendMessage("Success " + teamAbbreviation + " Amount: " + betAmount).queue();
                } else {
                    event.getHook().sendMessage("Error " + teamAbbreviation + " Amount: " + betAmount).queue();
                }
            }catch (Exception e) {
                LogManager.error("Error receiving bet command", e.getStackTrace().toString());
            }
        }
    }
}
