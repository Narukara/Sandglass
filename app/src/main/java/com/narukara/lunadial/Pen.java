package com.narukara.lunadial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Pen {

    public static File fileDir;
    public static File cache;

    public static String read(File file, String key) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                String tempString = bufferedReader.readLine();
                if (tempString == null) {
                    bufferedReader.close();
                    return null;
                }
                if (tempString.substring(0, tempString.indexOf(":")).equals(key)) {
                    bufferedReader.close();
                    return tempString.substring(tempString.indexOf(":") + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void write(File file, String key, String value) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder(key + ":" + value + "\r\n");
            while (true) {
                String tempString = bufferedReader.readLine();
                if (tempString == null) {
                    bufferedReader.close();
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false));
                    bufferedWriter.write(stringBuilder.toString());
                    bufferedWriter.close();
                    break;
                }
                if (!tempString.substring(0, tempString.indexOf(":")).equals(key)) {
                    stringBuilder.append(tempString + "\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
