package com.narukara.lunadial;

import java.io.IOException;

public abstract class Sandglass {
    private static long beginTime;

    public static void start() throws Exception {
        if (beginTime != -1) {
            throw new Exception("invalid beginTime");
        }
        beginTime = System.currentTimeMillis();
        Pen.write(Pen.cache, "beginTime", String.valueOf(beginTime));
    }

    public static void reload() throws IOException {
        String string = Pen.read(Pen.cache, "beginTime");
        if (string == null) {
            beginTime = -1;
        } else {
            beginTime = Long.parseLong(string);
        }
    }

    public static int[] getDuration() {
        return secondsToHHMM((System.currentTimeMillis() - beginTime) / 1000);
    }

    public static int[] secondsToHHMM(long seconds) {
        int[] HHMM = new int[2];
        HHMM[0] = (int) (seconds / 3600);
        HHMM[1] = (int) (seconds / 60 - HHMM[0] * 60);
        return HHMM;
    }

    public static long end() throws Exception {
        long endTime = System.currentTimeMillis();
        if (beginTime == -1) {
            throw new Exception("invalid beginTime");
        }
        long duration = (endTime - beginTime) / 1000;
        Pen.write(Pen.cache, "beginTime", "-1");
        beginTime = -1;
        return (duration / 60) + ((duration % 60 > 29) ? 1 : 0);
    }
}
