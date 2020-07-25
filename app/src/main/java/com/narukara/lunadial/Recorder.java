package com.narukara.lunadial;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public abstract class Recorder {

    public static void commit(int id, long time) throws IOException {
        File target = new File(Pen.fileDir, getDate());
        if (!target.exists()) {
            target.createNewFile();
            Pen.write(target, String.valueOf(id), String.valueOf(time));
        } else {
            String already = Pen.read(target, String.valueOf(id));
            if (already == null) {
                Pen.write(target, String.valueOf(id), String.valueOf(time));
            } else {
                Pen.write(target, String.valueOf(id), String.valueOf(time + Long.parseLong(already)));
            }
        }

    }

    private static String getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        return calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE);
    }
}
