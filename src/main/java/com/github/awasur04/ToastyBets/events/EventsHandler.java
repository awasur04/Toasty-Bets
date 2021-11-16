package com.github.awasur04.ToastyBets.events;

import com.github.awasur04.ToastyBets.discord.DiscordService;
import com.github.awasur04.ToastyBets.update.UpdateGames;
import com.github.awasur04.ToastyBets.utilities.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Component
public class EventsHandler {
    @Autowired
    private DiscordService discordService;
    @Autowired
    private UpdateGames updateGames;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStart() {
        try {
            discordService.startBot();
            updateGames.updateSchedule();
        }catch (Exception e) {
            LogManager.error("Main Program: ", e.getStackTrace().toString());
        }
    }

    @PreDestroy
    public void onApplicationClose() {
        LogManager.log("Shutting Down");
        discordService.stopBot();
        LogManager.closeLogs();
    }
}
