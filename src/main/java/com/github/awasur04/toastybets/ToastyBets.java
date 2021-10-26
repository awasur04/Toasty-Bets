package com.github.awasur04.toastybets;

import com.github.awasur04.toastybets.database.DatabaseManager;
import com.github.awasur04.toastybets.discord.CommandHandler;
import com.github.awasur04.toastybets.discord.DiscordManager;
import com.github.awasur04.toastybets.managers.GameManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;


public class ToastyBets {


    public static void main(String[] args) throws LoginException {
        //START GAME MANAGER
        GameManager gm = new GameManager(args[0]);

        //SHUTDOWN HOOK TO CLOSE PROPERLY
        Runtime.getRuntime().addShutdownHook(new Thread(gm::closeProgram));
    }
}
