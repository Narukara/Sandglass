package com.narukara.lunadial;

import java.io.IOException;

public abstract class Sandglass {
    private static long beginTime;
    private static int id;

    public static void start(int id) throws Exception {
        if (!Acts.isIDValid(id)) {
            throw new Exception("invalid id");
        }
        if (beginTime != -1) {
            throw new Exception("invalid beginTime(already running)");
        }
        Sandglass.id = id;
        beginTime = System.currentTimeMillis();
        Pen.write(Pen.cache, "beginTime", String.valueOf(beginTime));
        Pen.write(Pen.cache, "id", String.valueOf(Sandglass.id));
    }

    public static void reload() throws IOException {
        String string = Pen.read(Pen.cache, "beginTime");
        if (string == null) {
            beginTime = -1;
        } else {
            beginTime = Long.parseLong(string);
        }
        string = Pen.read(Pen.cache, "id");
        if (string == null) {
            id = -1;
        } else {
            id = Integer.parseInt(string);
        }
    }

    public static int[] getDuration() {
        return Tools.secondsToHHMM((System.currentTimeMillis() - beginTime) / 1000);
    }

    public static int getID() {
        return id;
    }

    public static boolean isRunning() {
        return beginTime != -1;
    }

    public static void changeID(int id) throws Exception {
        if(!Acts.isIDValid(id) || id == -1){
            throw new Exception("invalid id");
        }
        Sandglass.id = id;
    }

    public static void end() throws Exception {
        if (beginTime == -1) {
            id = -1;
            throw new Exception("invalid beginTime(not running)");
        }
        if (!Acts.isIDValid(id)) {
            beginTime = -1;
            id = -1;
            throw new Exception("invalid id");
        }
        long endTime = System.currentTimeMillis();
        long duration = (endTime - beginTime) / 1000;
        Recorder.commit(id, (duration / 60) + ((duration % 60 > 29) ? 1 : 0));
        Pen.write(Pen.cache, "beginTime", "-1");
        Pen.write(Pen.cache, "id", "-1");
        beginTime = -1;
        id = -1;
    }
}
