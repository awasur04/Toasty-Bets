package com.github.awasur04.ToastyBets.events;

import com.github.awasur04.ToastyBets.discord.DiscordService;
import com.github.awasur04.ToastyBets.discord.ResponseHandler;
import com.github.awasur04.ToastyBets.game.GameManager;
import com.github.awasur04.ToastyBets.update.UpdateGames;
import com.github.awasur04.ToastyBets.utilities.LogManager;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class EventsHandler {
    @Autowired
    private DiscordService discordService;
    @Autowired
    private UpdateGames updateGames;
    @Autowired
    private GameManager gameManager;
    @Autowired
    private ScheduledEventsManager scheduledEventsManager;
    @Autowired
    private ResponseHandler responseHandler;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStart() {
        try {
            LogManager.initialize();
            LogManager.log("Program Starting");
            updateGames.updateSchedule();
            updateGames.updateOdds();
            gameManager.registerActiveBets();
            scheduledEventsManager.initialize();
            responseHandler.resetCachedList();
            discordService.startBot();
            updateGames.updateScore();
            gameManager.sendAllUsersBets();
        }catch (Exception e) {
            LogManager.error("Main Program: ", e.getMessage());
        }
    }

    @PreDestroy
    public void onApplicationClose() {
        LogManager.log("Program Shutting Down");
        discordService.stopBot();
        scheduledEventsManager.shutdownExecutor();
        LogManager.closeLogs();
        LogManager.log("Shutdown Complete\n");
    }


    public void nextWeek() {
        LogManager.log("nextWeek called");
        responseHandler.resetCachedList();
        updateGames.updateSchedule();
        updateGames.updateOdds();
        scheduledEventsManager.weeklyUpdateReset();
        scheduledEventsManager.addWeeklyEvents();
        gameManager.payWeeklyUsers();
        gameManager.sendAllUsersSchedule();
    }

    public void oddsUpdate() {
        updateGames.updateOdds();
        gameManager.sendAllUsersSchedule();
    }

    public void gameCheck() {
        gameManager.checkGameStart();
    }

    public void nextDay() {
        LogManager.log("nextDay called");
        scheduledEventsManager.dailyUpdateReset();
        scheduledEventsManager.addDailyEvents();
    }


    public void scoreUpdate() {
        LogManager.log("scoreUpdate called");
        updateGames.updateScore();
        gameManager.sendAllUsersSchedule();
    }

}
