package com.github.awasur04.ToastyBets.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
public class DiscordService {

    private JDA jda;

    @Value("${discord.token}")
    private String discordToken;

    public void startBot() throws LoginException {
        this.jda = JDABuilder.createDefault(this.discordToken)
                .setActivity(Activity.playing("Coming soon ;)"))
                .addEventListeners(new CommandHandler())
                .build();

        registerCommands(
                new CommandData("join", "Join Toasty Bets!"),
                new CommandData("timezone", "Change your timezone").addOption(OptionType.STRING, "timezone", "Input your desired timezone", true),
                new CommandData("bet", "Place a bet").addOption(OptionType.STRING, "team_abbreviation", "Team you want to bet on", true).addOption(OptionType.STRING, "amount", "Amount you would like to bet", true)
        );
    }

    public void stopBot() {
        this.jda.shutdown();
    }

    public void registerCommands(CommandData... commandData) {
        for (CommandData command : commandData) {
            this.jda.upsertCommand(command).queue();
        }
    }
    public JDA getJda() {
        return jda;
    }
}
