package com.narukara.lunadial;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class Recorder {

    public static void commit(int id, long time) throws IOException {
        File xls = new File(Pen.fileDir, Tools.getYear() + ".xls");
        if (!xls.exists()) {
            initXLS(xls);
        }
        time += (long) readXLS(xls, Tools.getMonth(), id + 1, Tools.getDate());
        writeXLS(xls, Tools.getMonth(), id + 1, Tools.getDate(), String.valueOf(time));
    }

    public static void initXLS(File file) throws IOException {
        try (HSSFWorkbook hssfWorkbook = new HSSFWorkbook()) {
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
        }
    }

    public static void writeXLS(File file, int sheet, int row, int column, String value) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fileInputStream)) {
            fileInputStream.close();
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheet);
            HSSFRow hssfRow = hssfSheet.getRow(row);
            if (hssfRow == null) {
                hssfRow = hssfSheet.createRow(row);
            }
            hssfRow.createCell(column).setCellValue(value);
            hssfWorkbook.write(file);
        }
    }

    public static double readXLS(File file, int sheet, int row, int column) throws IOException {
        try (HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(file))) {
            HSSFCell hssfCell = hssfWorkbook.getSheetAt(sheet).getRow(row).getCell(column);
            if (hssfCell.getCellType() == CellType.STRING) {
                return Long.parseLong(hssfCell.getStringCellValue());
            } else {
                return hssfCell.getNumericCellValue();
            }
        } catch (NullPointerException e) {
            return 0;
        }
    }

}
