package com.github.awasur04.toastybets.discord;

import com.github.awasur04.toastybets.managers.GameManager;
import com.github.awasur04.toastybets.managers.LogManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {
    private DiscordManager discordManager;

    public CommandHandler(DiscordManager dm) {
        this.discordManager = dm;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equalsIgnoreCase("join")) {
            event.deferReply().queue();
            User source = event.getUser();
            if (discordManager.checkIfUserIsRegistered(source.getId())) {
                event.getMember().getUser().openPrivateChannel().flatMap(channel -> channel.sendMessage("You are already a member")).queue();
            } else {
                if (discordManager.createUser(source.getId(), source.getName())) {
                    event.getMember().getUser().openPrivateChannel().flatMap(channel -> channel.sendMessage("Welcome thank you for joining!")).queue();
                } else {
                    event.getMember().getUser().openPrivateChannel().flatMap(channel -> channel.sendMessage("Please try again later")).queue();
                }
            }
            event.getHook().sendMessage("Please check your dm's").queue();
        }
    }
}
