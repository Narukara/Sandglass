package com.narukara.lunadial;

import java.util.Calendar;
import java.util.TimeZone;

public abstract class Tools {
    public static String notNullMessage(String message) {
        if (message == null) {
            return "null message";
        }
        return message;
    }

    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        return calendar.get(Calendar.MONTH);
    }

    public static int getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        return calendar.get(Calendar.DATE);
    }

    public static int[] secondsToHHMM(long seconds) {
        int[] HHMM = new int[2];
        HHMM[0] = (int) (seconds / 3600);
        HHMM[1] = (int) (seconds / 60 - HHMM[0] * 60);
        return HHMM;
    }
}
