package com.github.awasur04.toastybets.utilities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateFormat {

    public static String formatDate(LocalDateTime dateTime) {
        LocalDate eventDate = dateTime.toLocalDate();
        LocalTime eventTime = dateTime.toLocalTime();

        StringBuilder sb = new StringBuilder();

        switch(eventDate.getDayOfWeek()) {
            case SUNDAY -> sb.append("Sunday ");
            case MONDAY -> sb.append("Monday ");
            case TUESDAY -> sb.append("Tuesday ");
            case WEDNESDAY -> sb.append("Wednesday ");
            case THURSDAY -> sb.append("Thursday ");
            case FRIDAY -> sb.append("Friday ");
            case SATURDAY -> sb.append("Saturday ");
        }

        switch(eventDate.getMonth()) {
            case JANUARY -> sb.append("Jan ");
            case FEBRUARY -> sb.append("Feb ");
            case MARCH -> sb.append("Mar ");
            case APRIL -> sb.append("Apr ");
            case MAY -> sb.append("May ");
            case JUNE -> sb.append("Jun ");
            case JULY -> sb.append("Jul ");
            case AUGUST -> sb.append("Aug ");
            case SEPTEMBER -> sb.append("Sep ");
            case OCTOBER -> sb.append("Oct ");
            case NOVEMBER -> sb.append("Nov ");
            case DECEMBER -> sb.append("Dec ");
        }
        int day = eventDate.getDayOfMonth();
        switch (day) {
            case 1,21,31 -> sb.append(day + "st");
            case 2,22,32 -> sb.append(day + "nd");
            case 3,23 -> sb.append(day + "rd");
            default -> sb.append(day + "th");
        }

        sb.append(" at ");

        switch(eventTime.getHour()) {
            case 1,13 -> sb.append("1:");
            case 2,14 -> sb.append("2:");
            case 3,15 -> sb.append("3:");
            case 4,16 -> sb.append("4:");
            case 5,17 -> sb.append("5:");
            case 6,18 -> sb.append("6:");
            case 7,19 -> sb.append("7:");
            case 8,20 -> sb.append("8:");
            case 9,21 -> sb.append("9:");
            case 10,22 -> sb.append("10:");
            case 11,23 -> sb.append("11:");
            case 12,24 -> sb.append("12:");
        }


        switch(eventTime.getMinute()) {
            case 0 -> sb.append("00");
            case 1,2,3,4,5,6,7,8,9 -> sb.append("0" + eventTime.getMinute());
            default -> sb.append(eventTime.getMinute());
        }

        if (eventTime.getHour() < 12) {
            sb.append("am");
        } else {
            sb.append("pm");
        }


        return sb.toString();
    }
}
