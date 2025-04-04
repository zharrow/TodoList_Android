package com.example.todolist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_DATE_TIME_FORMAT = new SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DAY_OF_WEEK_FORMAT = new SimpleDateFormat("EEEE", Locale.getDefault());

    /**
     * Convertit une chaîne de date au format "yyyy-MM-dd" en objet Date
     */
    public static Date parseDate(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * Convertit une chaîne de date au format "yyyy-MM-dd HH:mm" en objet Date
     */
    public static Date parseDateTime(String dateTimeString) {
        try {
            return DATE_TIME_FORMAT.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * Convertit un objet Date en chaîne au format "yyyy-MM-dd"
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * Convertit un objet Date en chaîne au format "yyyy-MM-dd HH:mm"
     */
    public static String formatDateTime(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }

    /**
     * Convertit un objet Date en chaîne au format "d MMM yyyy" pour l'affichage
     */
    public static String formatDisplayDate(Date date) {
        return DISPLAY_DATE_FORMAT.format(date);
    }

    /**
     * Convertit un objet Date en chaîne au format "HH:mm" pour l'affichage
     */
    public static String formatDisplayTime(Date date) {
        return DISPLAY_TIME_FORMAT.format(date);
    }

    /**
     * Convertit un objet Date en chaîne au format "d MMM yyyy, HH:mm" pour l'affichage
     */
    public static String formatDisplayDateTime(Date date) {
        return DISPLAY_DATE_TIME_FORMAT.format(date);
    }

    /**
     * Retourne la date d'aujourd'hui au format "yyyy-MM-dd"
     */
    public static String getTodayDateString() {
        return DATE_FORMAT.format(new Date());
    }

    /**
     * Retourne la date de demain au format "yyyy-MM-dd"
     */
    public static String getTomorrowDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return DATE_FORMAT.format(calendar.getTime());
    }

    /**
     * Retourne la date d'hier au format "yyyy-MM-dd"
     */
    public static String getYesterdayDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return DATE_FORMAT.format(calendar.getTime());
    }

    /**
     * Retourne la date d'il y a un mois au format "yyyy-MM-dd"
     */
    public static String getPastMonthDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        return DATE_FORMAT.format(calendar.getTime());
    }

    /**
     * Retourne la date d'une semaine dans le futur au format "yyyy-MM-dd"
     */
    public static String getNextWeekDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        return DATE_FORMAT.format(calendar.getTime());
    }

    /**
     * Calcule le nombre de jours entre deux dates
     */
    public static long getDaysBetween(Date startDate, Date endDate) {
        long diffInMillis = Math.abs(endDate.getTime() - startDate.getTime());
        return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Vérifie si une date est aujourd'hui
     */
    public static boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);

        return today.get(Calendar.YEAR) == calendarDate.get(Calendar.YEAR)
                && today.get(Calendar.DAY_OF_YEAR) == calendarDate.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Vérifie si une date est dans le futur
     */
    public static boolean isFutureDate(Date date) {
        return date.after(new Date());
    }

    /**
     * Vérifie si une date est dans le passé
     */
    public static boolean isPastDate(Date date) {
        return date.before(new Date());
    }

    /**
     * Retourne le jour de la semaine d'une date donnée en français
     */
    public static String getDayOfWeek(Date date) {
        return DAY_OF_WEEK_FORMAT.format(date);
    }

    /**
     * Retourne une représentation lisible relative à aujourd'hui (Aujourd'hui, Demain, Hier, etc.)
     */
    public static String getRelativeDateString(Date date) {
        if (isToday(date)) {
            return "Aujourd'hui";
        }

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);

        if (tomorrow.get(Calendar.YEAR) == calendarDate.get(Calendar.YEAR)
                && tomorrow.get(Calendar.DAY_OF_YEAR) == calendarDate.get(Calendar.DAY_OF_YEAR)) {
            return "Demain";
        }

        if (yesterday.get(Calendar.YEAR) == calendarDate.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == calendarDate.get(Calendar.DAY_OF_YEAR)) {
            return "Hier";
        }

        return DISPLAY_DATE_FORMAT.format(date);
    }
}