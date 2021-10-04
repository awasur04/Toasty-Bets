package com.github.awasur04.toastybets.managers;

import reactor.util.annotation.NonNull;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledEventsManager {
    private ScheduledExecutorService executorService;
    public static enum UpdateFrequency {
        NFLGAMEDAY, WEEKLY, BIDAILY, MONDAYNIGHT, THURSDAYNIGHT
    }

    public ScheduledEventsManager() {
        executorService = Executors.newScheduledThreadPool(5);
    }

    public void addEvent(@NonNull Runnable function, UpdateFrequency frequency) {
        switch(frequency){
            case NFLGAMEDAY:
                LocalDateTime sunday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusHours(10);
                long sundayOffset = LocalDateTime.now().until(sunday, ChronoUnit.HOURS);
                executorService.scheduleAtFixedRate(function, sundayOffset, 1, TimeUnit.HOURS);
                break;
            case BIDAILY:
                executorService.scheduleAtFixedRate(function, 0, 12, TimeUnit.HOURS);
                break;
            case WEEKLY:
                LocalDateTime wednesday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
                long wednesdayOffset = LocalDateTime.now().until(wednesday, ChronoUnit.HOURS);
                executorService.schedule(function, wednesdayOffset, TimeUnit.HOURS);
                break;
            case MONDAYNIGHT:
                LocalDateTime monday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusHours(20);
                long mondayOffset = LocalDateTime.now().until(monday, ChronoUnit.HOURS);
                executorService.scheduleAtFixedRate(function, mondayOffset, 1, TimeUnit.HOURS);
                break;
            case THURSDAYNIGHT:
                LocalDateTime thursday = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.THURSDAY)).plusHours(20);
                long thursdayOffset = LocalDateTime.now().until(thursday, ChronoUnit.HOURS);
                executorService.scheduleAtFixedRate(function, thursdayOffset, 1, TimeUnit.HOURS);
                break;
        }
    }

    public void resetExecutor() {
        executorService.shutdown();
        executorService = Executors.newScheduledThreadPool(5);
    }

    public void shutdownExecutor() {
        this.executorService.shutdown();
    }
}
