package com.narukara.lunadial;

public abstract class Acts {
    public final static int SLEEP = 0;
    public final static int EAT = 1;
    public final static int CLEAN = 2;
    public final static int STUDY = 3;
    public final static int FUN = 4;
    public final static int OTHER = 5;

    private final static String[] ActName = {"就寝", "就餐", "内务", "学习", "娱乐", "其他"};

    public static String getActName(int id) {
        if (id == -1) {
            return "ID error";
        }
        return ActName[id];
    }

    public static boolean isIDValid(int id) {
        return id >= 0 && id <= 5;
    }
}
