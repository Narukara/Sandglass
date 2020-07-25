package com.narukara.lunadial;

public abstract class Sandglass {
    private static long beginTime, endTime;

    public static void start() {
        beginTime = System.currentTimeMillis();
        Pen.write(Pen.cache, "beginTime", String.valueOf(beginTime));
    }

    public static void reload() {
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

    public static long end() {
        endTime = System.currentTimeMillis();
        if(beginTime == -1){
            return 0;
        }
        Pen.write(Pen.cache, "beginTime", "-1");
        return (endTime - beginTime) / 1000;
    }
}
