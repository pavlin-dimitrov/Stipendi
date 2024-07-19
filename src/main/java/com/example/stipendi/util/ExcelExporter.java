package com.example.stipendi.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {

    public static Workbook createWorkbook() {
        return new XSSFWorkbook();
    }

    public static void createSheetWithHeaders(Workbook workbook, String sheetName, List<String> headers) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(createHeaderCellStyle(workbook));
        }
    }

    private static CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public static void writeDataToSheet(Sheet sheet, List<List<Object>> data) {
        int rowCount = 1; // Start after headers
        for (List<Object> rowData : data) {
            Row row = sheet.createRow(rowCount++);
            for (int i = 0; i < rowData.size(); i++) {
                Cell cell = row.createCell(i);
                setCellValue(cell, rowData.get(i));
            }
        }
    }

    private static void setCellValue(Cell cell, Object value) {
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
    }

    public static void saveWorkbook(Workbook workbook, String filePath) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
    }
}
