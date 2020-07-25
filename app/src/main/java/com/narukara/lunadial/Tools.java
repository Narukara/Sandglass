package com.narukara.lunadial;

public abstract class Tools {
    public static String notNullMessage(String message) {
        if (message == null) {
            return "null message";
        }
        return message;
    }
}
