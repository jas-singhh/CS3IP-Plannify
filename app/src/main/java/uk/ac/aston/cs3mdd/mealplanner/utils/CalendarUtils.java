package uk.ac.aston.cs3mdd.mealplanner.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarUtils {
    // reference: https://github.com/codeWithCal/CalendarTutorialAndroidStudio/blob/WeeklyCalendar/app/src/main/java/codewithcal/au/calendarappexample/CalendarUtils.java


    /**
     * Returns the most recent Monday's date within the most recent week in which the given date
     * is contained.
     *
     * @param date date given to calculate the date of the Monday in the week.
     * @return the date of the most recent monday in which the given date is contained.
     */
    public static LocalDate getLastMondayFromDate(LocalDate date) {
        if (date== null) return null;

        LocalDate lastWeek = date.minusWeeks(1);

        while(date.isAfter(lastWeek)) {
            if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                return date;
            }

            date = date.minusDays(1);
        }

        return null;
    }

    /**
     * Returns the week days commencing from Monday for the week containing the given date.
     *
     * @param date date used to compute the weekdays.
     * @return a list of week days for the week containing the given date.
     */
    public static ArrayList<LocalDate> getDatesInWeekBasedOnDate(LocalDate date) {
        if (date == null) return null;
        ArrayList<LocalDate> result = new ArrayList<>(7);// week - 7 days
        LocalDate firstDayOfWeek = getLastMondayFromDate(date);
        LocalDate endDate = firstDayOfWeek.plusWeeks(1);
        while(firstDayOfWeek.isBefore(endDate)) {
            result.add(firstDayOfWeek);
            firstDayOfWeek = firstDayOfWeek.plusDays(1);
        }
        return result;
    }

    /**
     * Returns a different version of the given date in the format of "Mon 15"
     *
     * @param date date to format.
     * @return the formatted date in the format EEE dd.
     */
    public static String getFormattedDayNameAndDayFromDate(LocalDate date) {
        if (date == null) return null;

        DateTimeFormatter df = DateTimeFormatter.ofPattern("EEE dd");
        return df.format(date);
    }

    /**
     * Returns a different version of the given date in the format of "January 2024", by
     * basing the calculation on the most recent Monday within the week in which
     * the given date was contained.
     *
     * @param date date to format.
     * @return formatted date in the format of MMMM yyyy.
     */
    public static String getFormattedMonthYearFromDate(LocalDate date) {
        if (date == null) return null;

        date = getLastMondayFromDate(date);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM yyyy");
        return df.format(date);
    }

    /**
     * Returns a different version of the given date in the format of "Mon 01 January 2024".
     *
     * @param date date to format.
     * @return formatted version of the date in the format of EEE dd MMMM yyyy.
     */
    public static String getFormattedDayMonthYearFromDate(LocalDate date) {
        if (date != null) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("EEE dd MMMM yyyy");
            return df.format(date);
        }

        return null;
    }
}
