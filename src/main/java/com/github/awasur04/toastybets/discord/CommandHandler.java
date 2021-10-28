package com.github.awasur04.toastybets.discord;

import com.github.awasur04.toastybets.models.User;
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
        if (event.getName().equalsIgnoreCase("join")) {
            event.deferReply().queue();
            net.dv8tion.jda.api.entities.User source = event.getUser();
            if (discordManager.checkIfUserIsCreated(source.getId())) {
                //discordManager.printWeeklySchedule(event.getUser().getId());
                source.openPrivateChannel().flatMap(channel -> channel.sendMessage("You are not able to join once already registered, if you believe this is an error please reach out to an admin.")).queue();
            } else {
                discordManager.createUser(source.getId(), source.getName());
            }
            event.getHook().sendMessage("Please check your dm's").queue();
        }else if (event.getName().equalsIgnoreCase("register")) {
            event.deferReply().queue();

            net.dv8tion.jda.api.entities.User source = event.getUser();
            String updateTimeZone = event.getOption("timezone").getAsString().toUpperCase();

            if (!discordManager.checkIfUserIsCreated(source.getId())) {
                discordManager.createUser(source.getId(), source.getName());
            }

            User newUser = discordManager.getUser(source.getId());

            if (discordManager.updateTimeZone(newUser, updateTimeZone)) {
                discordManager.displayHelp(newUser.getDiscordId().toString());
                discordManager.printWeeklySchedule(newUser.getDiscordId().toString());
                event.getHook().sendMessage("Success").queue();
            } else {
                event.getHook().sendMessage("Invalid Time zone, please try again").queue();
            }
        }
    }
}
