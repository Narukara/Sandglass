package com.narukara.lunadial;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public abstract class Recorder {

    public static void commit(int id, long time) throws IOException {
        File xls = new File(Pen.fileDir, getYear() + ".xls");
        if (!xls.exists()) {
            initXLS(xls);
        }
    }

    public static void initXLS(File file) throws IOException {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet[] hssfSheet = new HSSFSheet[12];
        for (int i = 1; i <= 12; i++) {
            hssfSheet[i - 1] = hssfWorkbook.createSheet(String.valueOf(i));
            HSSFRow hssfRow1 = hssfSheet[i - 1].createRow(0);
            for (int j = 1; j <= 31; j++) {
                hssfRow1.createCell(j).setCellValue(j);
            }
            for (int j = 0; j < 6; j++) {
                hssfSheet[i - 1].createRow(j + 1).createCell(0).setCellValue(Acts.getActName(j));
            }
        }
        hssfWorkbook.write(file);
        hssfWorkbook.close();
    }

//    private static String readXLS(File file, int row, int cell) {
//    }

    private static String getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    private static String getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        return String.valueOf(calendar.get(Calendar.MONTH));
    }

    private static String getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        return String.valueOf(calendar.get(Calendar.DATE));
    }
}
