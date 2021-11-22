package com.github.awasur04.ToastyBets.events.temporaladjusters;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;

public class NextAfternoon implements TemporalAdjuster {
    @Override
    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.CLOCK_HOUR_OF_DAY, 11).with(ChronoField.MINUTE_OF_HOUR, 00);
    }
}
