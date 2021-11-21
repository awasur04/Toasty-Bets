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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
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
                //Run once at 2am on wednesday night
                LocalDateTime wednesday = LocalDateTime.now( ZoneId.of("America/New_York") ).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)).plusHours(2);
                long wednesdayOffset = LocalDateTime.now().until(wednesday, ChronoUnit.HOURS);
                currentEvents.put(UpdateFrequency.WEEKLY, executorService.schedule(function, wednesdayOffset, TimeUnit.HOURS));
                break;

            case ODDS:
                //Start at 7am and update every hour
                update(function, UpdateFrequency.ODDS, currentDate, null, new NextUpdate());
                break;

            case NIGHTLYRESET:
                //Run once every night at midnight
                LocalDateTime resetTime = LocalDateTime.now( ZoneId.of("America/New_York") ).with(new NextMidnight());
                long resetOffset = LocalDateTime.now().until(resetTime, ChronoUnit.MINUTES);
                currentEvents.put(UpdateFrequency.NIGHTLYRESET, executorService.schedule(function, resetOffset, TimeUnit.MINUTES));
                break;

            case THURSDAYSCORE:
                //Start at 6pm central on thursday night and update every 30 minutes
                update(function, frequency, currentDate, DayOfWeek.THURSDAY, new NextEvening());
                break;

            case MONDAYSCORE:
                //Start at 6pm central on monday night and update every 30 minutes
                update(function, frequency, currentDate, DayOfWeek.MONDAY, new NextEvening());
                break;

            case SUNDAYSCORE:
                //Start at 11am central on sunday and update every 30 minutes
                update(function, frequency, currentDate, DayOfWeek.SUNDAY, new NextAfternoon());
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
     * 30 minute dailt update (Odds, checkgamelock)
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
        currentEvents.get( UpdateFrequency.ODDS).cancel(false);

        currentEvents.remove(UpdateFrequency.NIGHTLYRESET);
        currentEvents.remove(UpdateFrequency.ODDS);
    }

    public void addDailyEvents() {
        addEvent(eventsHandler::oddsUpdate, UpdateFrequency.ODDS);
        addEvent(eventsHandler::nextDay, UpdateFrequency.NIGHTLYRESET);
    }

    public void update(@Nonnull Runnable function, UpdateFrequency updateFrequency, LocalDateTime dateTime, DayOfWeek dayOfWeek, TemporalAdjuster temporalAdjuster) {
        if (dateTime.getDayOfWeek() == dayOfWeek || dayOfWeek == null) {
            if (dateTime.getHour() < 11) {
                LocalDateTime desiredDay = LocalDateTime.now( ZoneId.of("America/New_York") ).with(new NextAfternoon());
                long desiredDayOffset = LocalDateTime.now().until(desiredDay, ChronoUnit.MINUTES);
                currentEvents.put(updateFrequency, executorService.scheduleAtFixedRate(function, desiredDayOffset, 30,TimeUnit.MINUTES));
            } else {
                currentEvents.put(updateFrequency, executorService.scheduleAtFixedRate(function, 0, 30,TimeUnit.MINUTES));
            }
        } else {
            LocalDateTime desiredDay = LocalDateTime.now( ZoneId.of("America/New_York") ).with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusHours(18);
            long desiredDayOffset = LocalDateTime.now().until(desiredDay, ChronoUnit.MINUTES);
            currentEvents.put(updateFrequency, executorService.scheduleAtFixedRate(function, desiredDayOffset, 30,TimeUnit.MINUTES));
        }
    }
}
