package com.github.awasur04.ToastyBets.events;

import com.github.awasur04.ToastyBets.events.temporaladjusters.NextAfternoon;
import com.github.awasur04.ToastyBets.events.temporaladjusters.NextEvening;
import com.github.awasur04.ToastyBets.events.temporaladjusters.NextMidnight;
import com.github.awasur04.ToastyBets.events.temporaladjusters.NextUpdate;
import com.github.awasur04.ToastyBets.models.enums.UpdateFrequency;
import com.github.awasur04.ToastyBets.utilities.LogManager;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledEventsManager {
    private ScheduledExecutorService executorService;
    private HashMap<UpdateFrequency, ScheduledFuture<?>> currentEvents;

    private static EventsHandler eventsHandler;

    @Autowired
    public void setEventsHandler(EventsHandler eventsHandler) {
        ScheduledEventsManager.eventsHandler = eventsHandler;
    }

    public void initialize() {
        currentEvents = new HashMap<>();
        executorService = Executors.newScheduledThreadPool(0);
        addWeeklyEvents();
        addDailyEvents();
        LogManager.log("Scheduled events registered");
    }

    public void addEvent(@Nonnull Runnable function, UpdateFrequency frequency) {

        LocalDateTime currentDate = LocalDateTime.now(ZoneId.of("America/New_York"));

        switch(frequency){
            case WEEKLY:
                //Run once at 2 on wednesday night
                LocalDate wednesdayDate = LocalDate.now( ZoneId.of("America/New_York") ).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
                LocalDateTime wednesday = wednesdayDate.atStartOfDay();
                long wednesdayOffset = LocalDateTime.now().until(wednesday, ChronoUnit.HOURS);
                LogManager.log("New weekly event added in " + wednesdayOffset + " hours");
                currentEvents.put(frequency, executorService.schedule(function, wednesdayOffset, TimeUnit.HOURS));
                break;

            case ODDS:
                //Start at 7am and update every hour
                update(function, frequency, currentDate, new NextUpdate(), 60);
                break;

            case GAMECHECK:
                //Start at 7am and update every 15 minutes
                update(function, frequency, currentDate,new NextUpdate(), 15);
                break;

            case NIGHTLYRESET:
                //Run once every night at midnight
                LocalDateTime resetTime = LocalDateTime.now( ZoneId.of("America/New_York") ).with(new NextMidnight());
                long resetOffset = LocalDateTime.now().until(resetTime, ChronoUnit.MINUTES);

                if (resetOffset < 0) {
                    resetOffset = resetOffset + 1440;
                }

                LogManager.log("Nightly reset added in " + resetOffset + " minutes");
                currentEvents.put(frequency, executorService.schedule(function, resetOffset, TimeUnit.MINUTES));
                break;

            case THURSDAYSCORE:
                //Start at 6pm central on thursday night and update every 30 minutes
                update(function, frequency, currentDate, DayOfWeek.THURSDAY, new NextEvening(), 30);
                break;

            case MONDAYSCORE:
                //Start at 6pm central on monday night and update every 30 minutes
                update(function, frequency, currentDate, DayOfWeek.MONDAY, new NextEvening(), 30);
                break;

            case SUNDAYSCORE:
                //Start at 11am central on sunday and update every 30 minutes
                update(function, frequency, currentDate, DayOfWeek.SUNDAY, new NextAfternoon(), 30);
                break;
        }
    }

    public void shutdownExecutor() {
          this.executorService.shutdown();
    }

    /**
     * Weekly Updates
     * weeklyscheduleupdate
     * MOnday score
     * Thursday score
     * Sunday score
     *
     * Daily Updates:
     * NightReset
     * 30 minute odds check update
     * 15 minute game check update
     */


    public void weeklyUpdateReset() {
        currentEvents.get(UpdateFrequency.WEEKLY).cancel(false);
        currentEvents.get(UpdateFrequency.MONDAYSCORE).cancel(false);
        currentEvents.get(UpdateFrequency.THURSDAYSCORE).cancel(false);
        currentEvents.get(UpdateFrequency.SUNDAYSCORE).cancel(false);

        currentEvents.remove(UpdateFrequency.WEEKLY);
        currentEvents.remove(UpdateFrequency.MONDAYSCORE);
        currentEvents.remove(UpdateFrequency.THURSDAYSCORE);
        currentEvents.remove(UpdateFrequency.SUNDAYSCORE);
    }

    public void addWeeklyEvents() {
        addEvent(eventsHandler::nextWeek, UpdateFrequency.WEEKLY);
        addEvent(eventsHandler::scoreUpdate, UpdateFrequency.THURSDAYSCORE);
        addEvent(eventsHandler::scoreUpdate, UpdateFrequency.SUNDAYSCORE);
        addEvent(eventsHandler::scoreUpdate, UpdateFrequency.MONDAYSCORE);
    }

    public void dailyUpdateReset() {
        currentEvents.get(UpdateFrequency.NIGHTLYRESET).cancel(false);
        currentEvents.get(UpdateFrequency.ODDS).cancel(false);
        currentEvents.get(UpdateFrequency.GAMECHECK).cancel(false);

        currentEvents.remove(UpdateFrequency.NIGHTLYRESET);
        currentEvents.remove(UpdateFrequency.ODDS);
        currentEvents.remove(UpdateFrequency.GAMECHECK);
    }

    public void addDailyEvents() {
        addEvent(eventsHandler::nextDay, UpdateFrequency.NIGHTLYRESET);
        addEvent(eventsHandler::oddsUpdate, UpdateFrequency.ODDS);
        addEvent(eventsHandler::gameCheck, UpdateFrequency.GAMECHECK);
    }

    public void update(@Nonnull Runnable function, UpdateFrequency updateFrequency, LocalDateTime dateTime, DayOfWeek dayOfWeek, TemporalAdjuster temporalAdjuster, int periodCooldown) {
        try {
            int desiredStartTime = 1;

            Random random = new Random();

            if (dayOfWeek == null) {
                desiredStartTime = 7;
            } else {
                switch(dayOfWeek) {
                    case SUNDAY -> desiredStartTime = 11;
                    case MONDAY -> desiredStartTime = 18;
                    case THURSDAY -> desiredStartTime = 18;
                }
            }

            if (dateTime.getHour() < desiredStartTime && dateTime.getDayOfWeek() != dayOfWeek) {
                LocalDate desiredDate = LocalDate.now( ZoneId.of("America/New_York") ).with(TemporalAdjusters.next(dayOfWeek));
                LocalDateTime desiredDay = desiredDate.atStartOfDay().with(temporalAdjuster);
                long desiredMinuteOffset = LocalDateTime.now().until(desiredDay, ChronoUnit.MINUTES);
                LogManager.log("Score added in " + desiredMinuteOffset + " minutes");
                currentEvents.put(updateFrequency, executorService.scheduleAtFixedRate(function, desiredMinuteOffset, periodCooldown,TimeUnit.MINUTES));
            } else {
                int intialDelay = random.nextInt(1,7);
                currentEvents.put(updateFrequency, executorService.scheduleAtFixedRate(function, intialDelay, periodCooldown,TimeUnit.MINUTES));
                LogManager.log("Score added in " + intialDelay + " minutes instantly");
            }
        } catch (Exception e) {
            LogManager.error("Failed to update event " + updateFrequency.name() + " ", e.getMessage());
        }
    }

    public void update(@Nonnull Runnable function, UpdateFrequency updateFrequency, LocalDateTime dateTime,TemporalAdjuster temporalAdjuster, int periodCooldown) {
        DayOfWeek currentDayOfWeek = dateTime.getDayOfWeek();
        update(function, updateFrequency, dateTime, currentDayOfWeek, temporalAdjuster, periodCooldown);
    }
}
