package com.github.awasur04.ToastyBets.discord;

import com.github.awasur04.ToastyBets.utilities.LogManager;
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
                .setActivity(Activity.playing("Use /register to help testing"))
                .addEventListeners(new CommandHandler())
                .build();

        LogManager.log("Discord service online");

        registerCommands(
                new CommandData("register", "Join Toasty Bets!"),
                new CommandData("timezone", "Change your timezone").addOption(OptionType.STRING, "timezone", "Input your desired timezone", true),
                new CommandData("schedule", "Print the Current Schedule"),
                new CommandData("bet", "Place a bet").addOption(OptionType.STRING, "team_abbreviation", "Team you want to bet on", true).addOption(OptionType.STRING, "amount", "Amount you would like to bet", true),
                new CommandData("dev", "Developer testing"),
                new CommandData("help", "Show help menu"),
                new CommandData("deactivate", "Deactivate your account (you can always activate it again using /register)"),
                new CommandData("balance", "Display your current balance"),
                new CommandData("report", "User to submit bugs, comments, or ideas").addOption(OptionType.STRING, "report_description", "Describe what you need to report here", true)
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
